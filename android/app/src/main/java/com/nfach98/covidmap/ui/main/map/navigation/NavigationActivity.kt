package com.nfach98.covidmap.ui.main.map.navigation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.services.android.navigation.ui.v5.NavigationViewOptions
import com.mapbox.services.android.navigation.ui.v5.OnNavigationReadyCallback
import com.mapbox.services.android.navigation.ui.v5.listeners.NavigationListener
import com.mapbox.services.android.navigation.ui.v5.listeners.RouteListener
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute
import com.mapbox.services.android.navigation.v5.routeprogress.ProgressChangeListener
import com.mapbox.services.android.navigation.v5.routeprogress.RouteProgress
import com.nfach98.covidmap.databinding.ActivityNavigationBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

import com.mapbox.turf.TurfConstants

import com.mapbox.turf.TurfMeasurement
import com.nfach98.covidmap.R
import com.nfach98.covidmap.api.ApiMain
import com.nfach98.covidmap.api.response.ResponseStatus
import com.nfach98.covidmap.session.UserToken
import com.nfach98.covidmap.ui.main.map.MapFragment


class NavigationActivity : AppCompatActivity(), OnNavigationReadyCallback, NavigationListener, RouteListener, ProgressChangeListener {

    private val lastKnownLocation: Location? = null
    private lateinit var origin: Point
    private lateinit var originText: String
    private lateinit var destination: Point
    private lateinit var destinationText: String

    private var token: String? = null

    private var isArrive = false
    private var is12Notified = false
    private var is34Notified = false
    private var isEnterNotified = false


    private var listConfirmed = ArrayList<Point>()
    private var initialDistances = ArrayList<Double>()

    private lateinit var binding: ActivityNavigationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(applicationContext, getString(R.string.mapbox_access_token))
        binding = ActivityNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listConfirmed = MapFragment.listConfirmed
        token = UserToken.getToken(applicationContext)

        originText = intent.getStringExtra("origin_text").toString()
        destinationText = intent.getStringExtra("destination_text").toString()
        val points = intent.getDoubleArrayExtra("points")
        if(points != null && points.size == 4){
            origin = Point.fromLngLat(points[1], points[0])
            destination = Point.fromLngLat(points[3], points[2])
        }

        for(point in listConfirmed) {
            val distance = TurfMeasurement.distance(
                point,
                Point.fromLngLat(origin.longitude(), origin.latitude()),
                TurfConstants.UNIT_METERS
            )
            initialDistances.add(distance)
        }

        with(binding){
            navigationView.onCreate(savedInstanceState)
            navigationView.initialize(this@NavigationActivity)
        }

        supportActionBar?.show()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.navigationView.onLowMemory()
    }

    override fun onStart() {
        super.onStart()
        binding.navigationView.onStart()
    }

    override fun onResume() {
        super.onResume()
        binding.navigationView.onResume()
    }

    override fun onStop() {
        super.onStop()
        binding.navigationView.onStop()
    }

    override fun onPause() {
        super.onPause()
        binding.navigationView.onPause()
    }

    override fun onDestroy() {
        binding.navigationView.onDestroy()
        super.onDestroy()
    }

    override fun onBackPressed() {
        if (!binding.navigationView.onBackPressed()) {
            super.onBackPressed()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        binding.navigationView.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        binding.navigationView.onRestoreInstanceState(savedInstanceState)
    }

    override fun onNavigationReady(isRunning: Boolean) {
        fetchRoute(origin, destination)
    }

    override fun onNavigationRunning() {

    }

    override fun onNavigationFinished() {

    }

    override fun onCancelNavigation() {
        binding.navigationView.stopNavigation()
        finish()
    }

    private fun fetchRoute(origin: Point, destination: Point) {
        NavigationRoute.builder(this)
            .accessToken(getString(R.string.mapbox_access_token))
            .origin(origin)
            .destination(destination)
            .alternatives(true)
            .build()
            .getRoute(object : Callback<DirectionsResponse?> {
                override fun onResponse(call: Call<DirectionsResponse?>, response: Response<DirectionsResponse?>) {
                    val directionsResponse = response.body()
                    if (directionsResponse != null && directionsResponse.routes().isNotEmpty()) {
                        startNavigation(directionsResponse.routes()[0])
                    }
                }

                override fun onFailure(call: Call<DirectionsResponse?>, t: Throwable) {
                    Timber.e(t)
                }
            })
    }

    private fun setupOptions(directionsRoute: DirectionsRoute): NavigationViewOptions? {
        val options = NavigationViewOptions.builder()
        options.directionsRoute(directionsRoute)
            .navigationListener(this)
            .progressChangeListener(this)
            .routeListener(this)
            .shouldSimulateRoute(true)
        return options.build()
    }

    private fun startNavigation(directionsRoute: DirectionsRoute) {
        val navigationViewOptions = setupOptions(directionsRoute)
        binding.navigationView.startNavigation(navigationViewOptions)
    }

    private fun sendAlert(kondisi: Int) {
        var contentText = ""

        when(kondisi) {
            0 -> {
                is12Notified = true
                is34Notified = false
                isEnterNotified = false
                contentText = "Peringatan, Anda mendekati daerah sebaran COVID-19"
            }
            1 -> {
                is12Notified = false
                is34Notified = true
                isEnterNotified = false
                contentText = "Awas, sebentar lagi Anda akan memasuki daerah sebaran COVID-19"
            }
            2 -> {
                is12Notified = false
                is34Notified = false
                isEnterNotified = true
                contentText = "Anda telah memasuki daerah sebaran COVID-19. Harap hati-hati dengan keadaan sekitar dan hindari kerumunan, serta terapkan protokol kesehatan yang berlaku"
            }
        }

        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val mBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_launcher_foreground))
            .setContentTitle("Peringatan Jarak")
            .setContentText(contentText)
            .setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
            channel.description = CHANNEL_NAME
            mBuilder.setChannelId(CHANNEL_ID)
            mNotificationManager.createNotificationChannel(channel)
        }

        val notification = mBuilder.build()
        mNotificationManager.notify(NOTIFICATION_ID, notification)
    }

    override fun onProgressChange(location: Location?, routeProgress: RouteProgress?) {
        if(location != null && !isArrive) {
            var initial = 0.0
            var nearest = 0.0

            listConfirmed.forEachIndexed { index, point ->
                val distance = TurfMeasurement.distance(
                    point,
                    Point.fromLngLat(location.longitude, location.latitude),
                    TurfConstants.UNIT_METERS
                )
                if(index == 0 || nearest > distance) {
                    nearest = distance
                    initial = initialDistances[index]
                }
            }

            when {
                nearest <= 15 -> {
                    if(!isEnterNotified) sendAlert(2)
                }
                nearest <= 0.25 * initial -> {
                    if(!is34Notified) sendAlert(1)
                }
                nearest <= 0.5 * initial -> {
                    if(!is12Notified) sendAlert(0)
                }
            }

            if(token != null) {
                ApiMain().services.updateKondisi(
                    token!!,
                    if(is12Notified) 1 else 0,
                    if(is34Notified) 1 else 0,
                    if(isEnterNotified) 1 else 0
                ).enqueue(object : Callback<ResponseStatus> {
                    override fun onResponse(call: Call<ResponseStatus>, response: Response<ResponseStatus>) {
                        if(response.isSuccessful) {
                            response.body().let {
                                if(it != null) {
                                    if(it.status == "success") {

                                    }
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<ResponseStatus>, t: Throwable) {
                        Log.e("API Exception: ", t.toString())
                    }
                })
            }
        }
    }

    override fun allowRerouteFrom(offRoutePoint: Point?): Boolean {
        return true
    }

    override fun onOffRoute(offRoutePoint: Point?) {
        TODO("Not yet implemented")
    }

    override fun onRerouteAlong(directionsRoute: DirectionsRoute?) {
        TODO("Not yet implemented")
    }

    override fun onFailedReroute(errorMessage: String?) {
        TODO("Not yet implemented")
    }

    override fun onArrival() {
        if(!isArrive) {
            isArrive = true
            Log.d("kondisi", "arrive")
            if (token != null) {
                ApiMain().services.addHistory(token!!, originText, destinationText).enqueue(object : Callback<ResponseStatus> {
                    override fun onResponse(call: Call<ResponseStatus>, response: Response<ResponseStatus>) {
                        /*if (response.code() == 200) {
                            response.body().let {
                                if(it != null) {

                                }
                            }
                        }*/
                    }

                    override fun onFailure(call: Call<ResponseStatus>, t: Throwable) {
                        Log.e("API Exception: ", t.toString())
                    }
                })
            }
        }
    }

    companion object {
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "channel_01"
        const val CHANNEL_NAME = "covidmap_channel"
    }
}