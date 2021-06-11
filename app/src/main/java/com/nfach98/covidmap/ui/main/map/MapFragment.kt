package com.nfach98.covidmap.ui.main.map

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.geojson.FeatureCollection
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.LineLayer
import com.mapbox.mapboxsdk.style.layers.Property
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.nfach98.covidmap.R
import com.nfach98.covidmap.api.ApiMain
import com.nfach98.covidmap.api.response.ResponseMap
import com.nfach98.covidmap.databinding.FragmentMapBinding
import com.nfach98.covidmap.session.UserToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.InputStream
import java.util.*

class MapFragment : Fragment(), OnMapReadyCallback, PermissionsListener {
    /*companion object {
        fun convertStreamToString(`is`: InputStream?): String {
            val scanner: Scanner = Scanner(`is`).useDelimiter("\\A")
            return if (scanner.hasNext()) scanner.next() else ""
        }
    }*/

    private var permissionsManager: PermissionsManager? = PermissionsManager(this)
    private var mapboxMap: MapboxMap? = null

    private lateinit var binding: FragmentMapBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Mapbox.getInstance(requireActivity(), getString(R.string.mapbox_access_token))

        binding = FragmentMapBinding.inflate(inflater)
        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)

        binding.fabLoc.setOnClickListener {
            mapboxMap?.style?.let { style -> enableLocationComponent(style) }
        }
        return binding.root
    }

    @SuppressWarnings("MissingPermission")
    private fun enableLocationComponent(style: Style) {
        if (PermissionsManager.areLocationPermissionsGranted(context)) {
            val locationComponent = mapboxMap?.locationComponent
            locationComponent?.activateLocationComponent(LocationComponentActivationOptions.builder(requireActivity(), style).build())
            locationComponent?.isLocationComponentEnabled = true
            locationComponent?.cameraMode = CameraMode.TRACKING
            locationComponent?.renderMode = RenderMode.COMPASS
        } else {
            permissionsManager = PermissionsManager(this)
            permissionsManager?.requestLocationPermissions(requireActivity())
        }
    }

    private fun drawLines(featureCollection: FeatureCollection) {
        if (mapboxMap != null) {
            mapboxMap?.getStyle { style: Style ->
                if (featureCollection.features() != null) {
                    if (featureCollection.features()!!.size > 0) {
                        style.addSource(GeoJsonSource("line-source", featureCollection))
                        style.addLayer(
                            LineLayer("linelayer", "line-source").withProperties(
                                PropertyFactory.lineCap(Property.LINE_CAP_SQUARE),
                                PropertyFactory.lineJoin(Property.LINE_JOIN_MITER),
                                PropertyFactory.lineOpacity(.7f),
                                PropertyFactory.lineWidth(7f),
                                PropertyFactory.lineColor(Color.parseColor("#3bb2d0")))
                        )
                    }
                }
            }
        }
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        mapboxMap.setStyle(Style.MAPBOX_STREETS) {
//            enableLocationComponent(it)
            val token = UserToken.getToken(requireActivity().applicationContext)
            if (token != null) {
                ApiMain().services.koordinatLine(token).enqueue(object : Callback<ResponseMap> {
                    override fun onResponse(call: Call<ResponseMap>, response: Response<ResponseMap>) {
                        if (response.code() == 200) {
                            response.body().let {
                                it?.featureCollection?.let { fc ->
                                    drawLines(FeatureCollection.fromJson(fc))
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<ResponseMap>, t: Throwable) {
                        Log.e("API Exception: ", t.toString())
                    }

                })
            }

            /*GlobalScope.launch(Dispatchers.IO) {

            }*/
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        permissionsManager!!.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
        Toast.makeText(context, "Error", Toast.LENGTH_LONG).show()
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            enableLocationComponent(mapboxMap?.style!!)
        } else {
            Toast.makeText(context, "Not granted", Toast.LENGTH_LONG).show()
            activity?.finish()
        }
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
    }
}