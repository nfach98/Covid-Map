package com.nfach98.covidmap.ui.main.map

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Color
import android.graphics.PointF
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.MapboxDirections
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.api.geocoding.v5.GeocodingCriteria
import com.mapbox.api.geocoding.v5.MapboxGeocoding
import com.mapbox.api.geocoding.v5.models.GeocodingResponse
import com.mapbox.core.constants.Constants.PRECISION_6
import com.mapbox.core.exceptions.ServicesException
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.CircleLayer
import com.mapbox.mapboxsdk.style.layers.LineLayer
import com.mapbox.mapboxsdk.style.layers.Property
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.*
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.nfach98.covidmap.api.ApiMain
import com.nfach98.covidmap.api.response.ResponseMap
import com.nfach98.covidmap.databinding.FragmentMapBinding
import com.nfach98.covidmap.session.UserToken
import com.nfach98.covidmap.ui.main.map.geolocation.GeolocationActivity
import com.nfach98.covidmap.ui.main.map.navigation.NavigationActivity
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import android.os.Looper.getMainLooper

import com.mapbox.android.core.location.LocationEngineRequest

import com.mapbox.android.core.location.LocationEngineProvider

import com.mapbox.android.core.location.LocationEngineResult

import com.mapbox.android.core.location.LocationEngineCallback
import java.lang.Exception
import java.lang.ref.WeakReference


class MapFragment : Fragment(), OnMapReadyCallback, PermissionsListener {

    private var permissionsManager: PermissionsManager? = PermissionsManager(this)
    var mapboxMap: MapboxMap? = null
    private var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>? = null

    private lateinit var binding: FragmentMapBinding

    private var hoveringMarker: ImageView? = null

    private var origin: Point? = null
    private var originText: String? = null
    private var destination: Point? = null
    private var destinationText: String? = null

    private var typePlace = 0

    private var isRoute = false
    private var isPicking = false
    private var isUserLocation = false

    var currentLocation: Location? = null
    private val callback: MapFragmentLocationCallback = MapFragmentLocationCallback(this)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        Mapbox.getInstance(requireActivity(), getString(com.nfach98.covidmap.R.string.mapbox_access_token))

        binding = FragmentMapBinding.inflate(inflater)
        with(binding){
            mapView.onCreate(savedInstanceState)
            mapView.getMapAsync(this@MapFragment)

            bottomSheetBehavior = BottomSheetBehavior.from(layoutBottomSheet)

            etFrom.clearFocus()
            etTo.clearFocus()

            fabRoute.setOnClickListener {
                if(origin != null && destination != null) {
                    val intent = Intent(requireActivity(), NavigationActivity::class.java)
                    val points = doubleArrayOf(origin!!.latitude(), origin!!.longitude(), destination!!.latitude(), destination!!.longitude())
                    intent.putExtra("points", points)
                    intent.putExtra("origin_text", originText)
                    intent.putExtra("destination_text", destinationText)
                    startActivity(intent)
                }
            }

            fabLoc.setOnClickListener {
                mapboxMap?.style?.let { style -> enableLocationComponent(style, true) }
            }

            etFrom.setOnFocusChangeListener { _, hasFocus ->
                if(hasFocus){
                    etFrom.clearFocus()
                    val intent = Intent(requireActivity(), GeolocationActivity::class.java)
                    intent.putExtra("type", 0)
                    intent.putExtra("hint", "Pilih titik awal")
                    intent.putExtra("feature_text", originText)
                    intent.putExtra("user_location", isUserLocation)
                    startActivityForResult(intent, GEO_REQUEST_CODE)
                }
            }

            etTo.setOnFocusChangeListener { _, hasFocus ->
                if(hasFocus){
                    etTo.clearFocus()
                    val intent = Intent(requireActivity(), GeolocationActivity::class.java)
                    intent.putExtra("type", 1)
                    intent.putExtra("hint", "Pilih tujuan")
                    intent.putExtra("feature_text", destinationText)
                    intent.putExtra("user_location", isUserLocation)
                    startActivityForResult(intent, GEO_REQUEST_CODE)
                }
            }

            backPick.setOnClickListener {
                layoutPick.visibility = View.GONE
                hoveringMarker?.visibility = View.GONE
                layoutRoute.visibility = View.VISIBLE
            }

            okPick.setOnClickListener {
                if (hoveringMarker?.visibility == View.VISIBLE) {
                    val target = mapboxMap?.cameraPosition?.target
                    hoveringMarker?.visibility = View.GONE
                    layoutPick.visibility = View.GONE
                    layoutRoute.visibility = View.VISIBLE

                    if(target != null) {
                        val text = reverseGeocode(Point.fromLngLat(target.longitude, target.latitude))
                        if(text == null) onPickLocation("${target.latitude}, ${target.longitude}", LatLng(target.latitude, target.longitude), typePlace)
                        else onPickLocation(text, LatLng(target.latitude, target.longitude), typePlace)
                    }
                }
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(origin != null && destination != null){
            val client = MapboxDirections.builder()
                .origin(origin!!)
                .destination(destination!!)
                .overview(DirectionsCriteria.OVERVIEW_FULL)
                .profile(DirectionsCriteria.PROFILE_DRIVING)
                .accessToken(getString(com.nfach98.covidmap.R.string.mapbox_access_token))
                .build()

            client?.enqueueCall(object : Callback<DirectionsResponse> {
                override fun onResponse(call: Call<DirectionsResponse>, response: Response<DirectionsResponse>) {
                    if (response.body() == null) {
                        Log.e("ROUTE:", "No routes found, make sure you set the right user and access token.")
                        return
                    } else if (response.body()!!.routes().size < 1) {
                        Log.e("ROUTE:", "No routes found")
                        return
                    }

                    val currentRoute = response.body()!!.routes()[0]

                    if (mapboxMap != null) {
                        mapboxMap!!.getStyle { style ->
                            val source = style.getSourceAs<GeoJsonSource>(ROUTE_SOURCE_ID)
                            source?.setGeoJson(LineString.fromPolyline(currentRoute.geometry()!!, PRECISION_6))
                        }
                    }
                }

                override fun onFailure(call: Call<DirectionsResponse>, throwable: Throwable) {
                    Log.e("ROUTE:","Error: " + throwable.message)
                }
            })
        }
    }

    @SuppressWarnings("MissingPermission")
    private fun enableLocationComponent(style: Style, isTracking: Boolean) {
        if (PermissionsManager.areLocationPermissionsGranted(context)) {
            val locationComponent = mapboxMap?.locationComponent
            locationComponent?.activateLocationComponent(LocationComponentActivationOptions.builder(requireActivity(), style).build())
            locationComponent?.isLocationComponentEnabled = true
            currentLocation = locationComponent?.lastKnownLocation

            if(isTracking){
                locationComponent?.cameraMode = CameraMode.TRACKING
                locationComponent?.renderMode = RenderMode.COMPASS
            }

            val locationEngine = LocationEngineProvider.getBestLocationEngine(requireContext())
            val request = LocationEngineRequest.Builder(1000L)
                .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(5000L).build()

            locationEngine.requestLocationUpdates(request, callback, getMainLooper())
            locationEngine.getLastLocation(callback)
        } else {
            permissionsManager = PermissionsManager(this)
            permissionsManager?.requestLocationPermissions(requireActivity())
        }
    }

    private fun drawLines(type: String, featureCollection: FeatureCollection) {
        var sourceId = ""
        var layerId = ""
        var colorString = ""

        when(type) {
            "GREEN" -> {
                sourceId = CONFIRM_GREEN_SOURCE_ID
                layerId = CONFIRM_GREEN_LAYER_ID
                colorString = "#0be25c"
            }
            "YELLOW" -> {
                sourceId = CONFIRM_YELLOW_LAYER_ID
                layerId = CONFIRM_YELLOW_LAYER_ID
                colorString = "#fee300"
            }
            "RED" -> {
                sourceId = CONFIRM_RED_LAYER_ID
                layerId = CONFIRM_RED_LAYER_ID
                colorString = "#ff0000"
            }
        }

        if (mapboxMap != null) {
            mapboxMap?.getStyle { style: Style ->
                if (featureCollection.features() != null) {
                    if (featureCollection.features()!!.size > 0) {
                        style.addSource(GeoJsonSource(sourceId, featureCollection))
                        style.addLayer(
                            LineLayer(layerId, sourceId).withProperties(
                                lineCap(Property.LINE_CAP_ROUND),
                                lineJoin(Property.LINE_JOIN_ROUND),
                                lineOpacity(.75f),
                                lineWidth(8f),
                                lineColor(Color.parseColor(colorString))
                            )
                        )
                    }
                }
            }
        }
    }

    private fun onClickLine(screenPoint: PointF) : Boolean {
        var features: List<Feature>? = null

        val featuresGreen = mapboxMap?.queryRenderedFeatures(screenPoint, CONFIRM_GREEN_LAYER_ID)
        if(featuresGreen!!.isNotEmpty()) features = featuresGreen

        val featuresYellow = mapboxMap?.queryRenderedFeatures(screenPoint, CONFIRM_YELLOW_LAYER_ID)
        if(featuresYellow!!.isNotEmpty()) features = featuresYellow

        val featuresRed = mapboxMap?.queryRenderedFeatures(screenPoint, CONFIRM_RED_LAYER_ID)
        if(featuresRed!!.isNotEmpty()) features = featuresRed

        return if (features != null && features.isNotEmpty()) {
            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED

            val geometry = JSONTokener(features[0].geometry()?.toJson()).nextValue() as JSONObject
            val coordinates = JSONArray(geometry["coordinates"].toString())
            val point = JSONArray(coordinates[0].toString())

            val properties = features[0].properties()
            val description = properties?.get("description").toString().replace("\"","")
            val confirm = description.split(",")

            with(binding){
                tvJalan.text = confirm[0]
                tvConfirm.text = "Jumlah terkonfirmasi: ${confirm[1]}"
            }

            centerMapTo(LatLng(point[1] as Double, point[0] as Double), 16.0)

            true
        }
        else {
            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
            false
        }
    }

    private fun centerMapTo(latlng: LatLng, zoom: Double) {
        val position = CameraPosition.Builder()
            .target(latlng)
            .zoom(zoom)
            .build()

        mapboxMap?.animateCamera(
            CameraUpdateFactory.newCameraPosition(position), 1000
        )
    }

    private fun onPickLocation(text: String, point: LatLng, type: Int) : Boolean {
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED

        var sourceId = ""

        if (mapboxMap != null) {
            when(type) {
                0 -> sourceId = SOURCE_FROM_ID
                1 -> sourceId = SOURCE_TO_ID
            }
            mapboxMap?.getStyle {
                it.getSourceAs<GeoJsonSource>(sourceId)?.setGeoJson(
                    FeatureCollection.fromFeatures(
                        arrayOf(
                            Feature.fromGeometry(Point.fromLngLat(point.longitude, point.latitude))
                        )
                    )
                )
            }

            if(type == 0) {
                with(binding.etFrom){
                    clearFocus()
                    setText(text)
                }
                originText = text
                origin = Point.fromLngLat(point.longitude, point.latitude)
            }
            else {
                with(binding.etTo){
                    clearFocus()
                    setText(text)
                }
                destinationText = text
                destination = Point.fromLngLat(point.longitude, point.latitude)
            }

            if(origin != null && destination != null) {
                createRoute()
                binding.fabRoute.visibility = View.VISIBLE
            }
            else {
                binding.fabRoute.visibility = View.GONE
            }
        }

        return true
    }

    private fun fetchConfirmed() {
        val token = UserToken.getToken(requireActivity().applicationContext)
        if (token != null) {
            ApiMain().services.koordinatLine(token).enqueue(object : Callback<ResponseMap> {
                override fun onResponse(call: Call<ResponseMap>, response: Response<ResponseMap>) {
                    if (response.code() == 200) {
                        response.body().let { map ->
                            map?.featureCollection?.let { fc ->
                                val featureCollection = FeatureCollection.fromJson(fc)

                                val greenFeatures = ArrayList<Feature>()
                                val yellowFeatures = ArrayList<Feature>()
                                val redFeatures = ArrayList<Feature>()

                                for (f in featureCollection.features()!!) {
                                    val properties = f.properties()
                                    val description = properties?.get("description").toString().replace("\"","")
                                    val confirm = description.split(",")[1].toInt()

                                    val lineString = f.geometry() as LineString

                                    when(confirm) {
                                        0 -> {
                                            greenFeatures.add(f)
                                        }
                                        1 -> {
                                            yellowFeatures.add(f)
                                            listConfirmed.add(lineString.coordinates()[0])
                                            listConfirmed.add(lineString.coordinates()[lineString.coordinates().size - 1])
                                        }
                                        in 2..Int.MAX_VALUE -> {
                                            redFeatures.add(f)
                                            listConfirmed.add(lineString.coordinates()[0])
                                            listConfirmed.add(lineString.coordinates()[lineString.coordinates().size - 1])
                                        }
                                    }
                                }

                                val greenCollection = FeatureCollection.fromFeatures(greenFeatures)
                                val yellowCollection = FeatureCollection.fromFeatures(yellowFeatures)
                                val redCollection = FeatureCollection.fromFeatures(redFeatures)

                                drawLines("GREEN", greenCollection)
                                drawLines("YELLOW", yellowCollection)
                                drawLines("RED", redCollection)
                            }

                            mapboxMap?.addOnMapClickListener { point ->
                                if(origin != null && destination != null){
                                    /*if(fromHasFocus) onPickLocation(point, binding.etFrom)
                                    else if(toHasFocus) onPickLocation(point, binding.etTo)

                                    true*/
                                }

                                else{
                                    mapboxMap?.projection?.toScreenLocation(point)?.let { onClickLine(it) }
                                }

                                false
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseMap>, t: Throwable) {
                    Log.e("API Exception: ", t.toString())
                }

            })
        }
    }

    private fun createRoute() {
        val client = MapboxDirections.builder()
            .origin(origin!!)
            .destination(destination!!)
            .overview(DirectionsCriteria.OVERVIEW_FULL)
            .profile(DirectionsCriteria.PROFILE_DRIVING)
            .accessToken(getString(com.nfach98.covidmap.R.string.mapbox_access_token))
            .build()

        client?.enqueueCall(object : Callback<DirectionsResponse> {
            override fun onResponse(call: Call<DirectionsResponse>, response: Response<DirectionsResponse>) {

                if (response.body() == null) {
                    Log.e("ROUTE:", "No routes found, make sure you set the right user and access token.")
                    return
                } else if (response.body()!!.routes().size < 1) {
                    Log.e("ROUTE:", "No routes found")
                    return
                }

                val currentRoute = response.body()!!.routes()[0]
                val waypoints = response.body()!!.waypoints()
                if(waypoints != null) {
                    val center = waypoints[waypoints.size / 2]
                    centerMapTo(
                        LatLng(center.location()!!.latitude(), center.location()!!.longitude()),
                        12.0
                    )
                }

                if (mapboxMap != null) {
                    mapboxMap!!.getStyle { style ->
                        val source = style.getSourceAs<GeoJsonSource>(ROUTE_SOURCE_ID)
                        source?.setGeoJson(LineString.fromPolyline(currentRoute.geometry()!!, PRECISION_6))
                    }
                }
            }

            override fun onFailure(call: Call<DirectionsResponse>, throwable: Throwable) {
                Log.e("ROUTE:","Error: " + throwable.message)
            }
        })
    }

    private fun reverseGeocode(point: Point) : String? {
        var name: String? = null

        try {
            val client = MapboxGeocoding.builder()
                .accessToken(getString(com.nfach98.covidmap.R.string.mapbox_access_token))
                .query(Point.fromLngLat(point.longitude(), point.latitude()))
                .geocodingTypes(GeocodingCriteria.TYPE_ADDRESS)
                .build()

            client.enqueueCall(object : Callback<GeocodingResponse?> {
                override fun onResponse(call: Call<GeocodingResponse?>, response: Response<GeocodingResponse?>) {
                    if (response.body() != null) {
                        val results = response.body()!!.features()
                        if (results.size > 0) {
                            val feature = results[0]
                            name = feature.placeName()
                        }
                    }
                }

                override fun onFailure(call: Call<GeocodingResponse?>, throwable: Throwable) {
                    Timber.e("Geocoding Failure: %s", throwable.message)
                }
            })
        } catch (servicesException: ServicesException) {
            Timber.e("Error geocoding: %s", servicesException.toString())
            servicesException.printStackTrace()
        }

        return name
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        mapboxMap.setStyle(Style.MAPBOX_STREETS) {
            enableLocationComponent(it, false)

            hoveringMarker = ImageView(requireContext())
            hoveringMarker?.setImageResource(com.nfach98.covidmap.R.drawable.ic_baseline_map_blue_24)
            val params = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER
            )
            hoveringMarker?.layoutParams = params
            binding.mapView.addView(hoveringMarker)
            hoveringMarker?.visibility = View.GONE

            fetchConfirmed()

            //Route
            val geoJsonSource = GeoJsonSource(
                ROUTE_SOURCE_ID, FeatureCollection.fromFeatures(arrayOf())
            )
            it.addSource(geoJsonSource)

            val lineLayer = LineLayer(ROUTE_LAYER_ID, ROUTE_SOURCE_ID)

            lineLayer.setProperties(
                lineCap(Property.LINE_CAP_ROUND),
                lineJoin(Property.LINE_JOIN_ROUND),
                lineWidth(8f),
                lineOpacity(.75f),
                lineColor(Color.parseColor("#0A42A6"))
            )
            it.addLayer(lineLayer)

            //From
            it.addSource(
                GeoJsonSource(SOURCE_FROM_ID, FeatureCollection.fromFeatures(arrayOf()))
            )
            it.addLayer(
                CircleLayer(LAYER_FROM_ID, SOURCE_FROM_ID).withProperties(
                    circleRadius(8f),
                    circleColor(Color.parseColor("#0A42A6")),
                    circleStrokeColor(Color.parseColor("#FFD70E")),
                    circleStrokeWidth(2f)
                )
            )

            //To
            it.addSource(
                GeoJsonSource(SOURCE_TO_ID, FeatureCollection.fromFeatures(arrayOf()))
            )
            it.addLayer(
                CircleLayer(LAYER_TO_ID, SOURCE_TO_ID).withProperties(
                    circleRadius(8f),
                    circleColor(Color.parseColor("#FFD70E")),
                    circleStrokeColor(Color.parseColor("#0A42A6")),
                    circleStrokeWidth(2f)
                )
            )

            /*it.addImage(
                RED_PIN_ICON_ID, BitmapUtils.getBitmapFromDrawable(
                    resources.getDrawable(com.nfach98.covidmap.R.drawable.red_marker)
                )
            )

            it.addLayer(
                SymbolLayer(ICON_LAYER_ID, ICON_SOURCE_ID).withProperties(
                    iconImage(RED_PIN_ICON_ID),
                    iconIgnorePlacement(true),
                    iconAllowOverlap(true),
                    iconOffset(arrayOf(0f, -9f))
                )
            )*/
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
            enableLocationComponent(mapboxMap?.style!!, false)
        } else {
            Toast.makeText(context, "Not granted", Toast.LENGTH_LONG).show()
            activity?.finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == GEO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                val type = data?.getIntExtra("type", 0)
                val resultText = data?.getStringExtra("feature_text")
                val resultPoints = data?.getDoubleArrayExtra("feature_points")
                val pick = data?.getBooleanExtra("pick", false)
                val userLocation = data?.getBooleanExtra("user_location", false)

                if(type != null) {
                    typePlace = type

                    if(userLocation != null && userLocation && currentLocation != null) {
                        onPickLocation("Lokasi Anda", LatLng(currentLocation!!.latitude, currentLocation!!.longitude), type)
                        isUserLocation = true
                    }

                    if(pick != null && pick) {
                        hoveringMarker?.visibility = View.VISIBLE
                        binding.layoutRoute.visibility = View.GONE
                        binding.layoutPick.visibility = View.VISIBLE
                    }

                    if(resultText != null && resultPoints != null){
                        onPickLocation(resultText, LatLng(resultPoints[0], resultPoints[1]), type)
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

    companion object {
        const val CONFIRM_GREEN_SOURCE_ID = "confirm-green-source-id"
        const val CONFIRM_GREEN_LAYER_ID = "confirm-green-layer-id"

        const val CONFIRM_YELLOW_SOURCE_ID = "confirm-yellow-source-id"
        const val CONFIRM_YELLOW_LAYER_ID = "confirm-yellow-layer-id"

        const val CONFIRM_RED_SOURCE_ID = "confirm-red-source-id"
        const val CONFIRM_RED_LAYER_ID = "confirm-red-layer-id"

        const val SOURCE_FROM_ID = "from-source-id"
        const val LAYER_FROM_ID = "from-layer-id"
        const val SOURCE_TO_ID = "to-source-id"
        const val LAYER_TO_ID = "to-layer-id"

        const val ROUTE_LAYER_ID = "route-layer-id"
        const val ROUTE_SOURCE_ID = "route-source-id"

        const val GEO_REQUEST_CODE = 100

        var listConfirmed = ArrayList<Point>()
        var listGreen = ArrayList<Point>()
        var listYellow = ArrayList<Point>()
        var listRed = ArrayList<Point>()
    }
}

private class MapFragmentLocationCallback(fragment: MapFragment?) : LocationEngineCallback<LocationEngineResult> {
    private val weakReference= WeakReference(fragment)

    override fun onSuccess(result: LocationEngineResult) {
        val fragment = weakReference.get()
        if (fragment != null) {
            val location = result.lastLocation ?: return
            fragment.currentLocation = location

            if (fragment.mapboxMap != null && result.lastLocation != null) {
                fragment.mapboxMap?.locationComponent!!.forceLocationUpdate(result.lastLocation)
            }
        }
    }

    override fun onFailure(exception: Exception) {

    }

}