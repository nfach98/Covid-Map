package com.nfach98.covidmap

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
import com.nfach98.covidmap.databinding.ActivityMainBinding
import java.io.InputStream
import java.util.*


class MainActivity : AppCompatActivity(), OnMapReadyCallback, PermissionsListener {
    private var permissionsManager: PermissionsManager? = PermissionsManager(this)
    private var mapboxMap: MapboxMap? = null

    private lateinit var binding: ActivityMainBinding

    companion object {
        fun convertStreamToString(`is`: InputStream?): String {
            val scanner: Scanner = Scanner(`is`).useDelimiter("\\A")
            return if (scanner.hasNext()) scanner.next() else ""
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token))
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)

        binding.fabLoc.setOnClickListener {
            mapboxMap?.style?.let { style -> enableLocationComponent(style) }
        }

        /*ApiMain().services.getKoordinatTitik().enqueue(object : Callback<ResponseKoordinatTitik> {
            override fun onResponse(call: Call<ResponseKoordinatTitik>, response: Response<ResponseKoordinatTitik>) {
                if (response.code() == 200) {
                    response.body().let {
                        val point = it?.lineCoordinats?.get(0)?.get(0)?.get(1)?.let { it1 -> Point(it.lineCoordinats[0][0][0], it1) }
                        val feature = Feature(point)
                        feature.identifier = "MyIdentifier"
                        feature.properties = JSONObject()
                        val geoJSON: JSONObject = feature.toJSON()
                    }
                }
            }

            override fun onFailure(call: Call<ResponseKoordinatTitik>, t: Throwable) {
                Log.e("API Exception: ", t.toString())
            }
        })*/
    }

    @SuppressWarnings("MissingPermission")
    private fun enableLocationComponent(style: Style) {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            val locationComponent = mapboxMap?.locationComponent
            locationComponent?.activateLocationComponent(LocationComponentActivationOptions.builder(this, style).build())
            locationComponent?.isLocationComponentEnabled = true
            locationComponent?.cameraMode = CameraMode.TRACKING
            locationComponent?.renderMode = RenderMode.COMPASS
        } else {
            permissionsManager = PermissionsManager(this)
            permissionsManager?.requestLocationPermissions(this)
        }
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        mapboxMap.setStyle(Style.MAPBOX_STREETS) {
            enableLocationComponent(it)
            /*GlobalScope.launch(Dispatchers.IO) {
                val featureCollection: FeatureCollection

                try {
                    val inputStream: InputStream = this@MainActivity.assets.open("example.geojson")
                    featureCollection = FeatureCollection.fromJson(convertStreamToString(inputStream))
                    withContext(Dispatchers.Main){
                        drawLines(featureCollection)
                    }
                } catch (exception: Exception) {
                    Log.e("Exception: ", exception.toString())
                }
            }*/
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        permissionsManager!!.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
        Toast.makeText(this, "Error", Toast.LENGTH_LONG).show()
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            enableLocationComponent(mapboxMap?.style!!)
        } else {
            Toast.makeText(this, "Not granted", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun drawLines(featureCollection: FeatureCollection) {
        if (mapboxMap != null) {
            mapboxMap?.getStyle { style: Style ->
                if (featureCollection.features() != null) {
                    if (featureCollection.features()!!.size > 0) {
                        style.addSource(GeoJsonSource("line-source", featureCollection))
                        style.addLayer(LineLayer("linelayer", "line-source")
                            .withProperties(PropertyFactory.lineCap(Property.LINE_CAP_SQUARE),
                                    PropertyFactory.lineJoin(Property.LINE_JOIN_MITER),
                                    PropertyFactory.lineOpacity(.7f),
                                    PropertyFactory.lineWidth(7f),
                                    PropertyFactory.lineColor(Color.parseColor("#3bb2d0"))))
                    }
                }
            }
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