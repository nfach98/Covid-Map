package com.nfach98.covidmap.ui.main.map.geolocation

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.mapbox.api.geocoding.v5.MapboxGeocoding
import com.mapbox.api.geocoding.v5.models.CarmenFeature
import com.mapbox.api.geocoding.v5.models.GeocodingResponse
import com.nfach98.covidmap.R
import com.nfach98.covidmap.databinding.ActivityGeolocationBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class GeolocationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGeolocationBinding
    private var type: Int = 0
    private var hint: String? = null
    private var text: String? = null
    private var isUserLocation = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGeolocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        type = intent.getIntExtra("type",0)
        hint = intent.getStringExtra("hint")
        text = intent.getStringExtra("feature_text")
        isUserLocation = intent.getBooleanExtra("user_location", false)

        supportActionBar?.hide()

        with(binding){
            back.setOnClickListener { finish() }

            etGeo.addTextChangedListener {
                if(it.toString().isNotEmpty()){
                    val mapboxGeocoding = MapboxGeocoding.builder()
                        .accessToken(getString(R.string.mapbox_access_token))
                        .query(it.toString())
                        .build()

                    mapboxGeocoding.enqueueCall(object : Callback<GeocodingResponse?> {
                        override fun onResponse(call: Call<GeocodingResponse?>?, response: Response<GeocodingResponse?>) {
                            val results = response.body()?.features()
                            if(results != null) {
                                if (results.size > 0) {
                                    val adapter = GeolocationAdapter()
                                    adapter.features = results as ArrayList<CarmenFeature>

                                    rvGeo.setHasFixedSize(true)
                                    rvGeo.layoutManager = LinearLayoutManager(this@GeolocationActivity)
                                    rvGeo.adapter = adapter

                                    adapter.setOnItemClickCallback(object : GeolocationAdapter.OnItemActionCallback {
                                        override fun onItemClicked(data: CarmenFeature) {
                                            val intent = Intent()
                                            intent.putExtra("feature_text", data.text())
                                            intent.putExtra("feature_points", doubleArrayOf(data.center()!!.latitude(), data.center()!!.longitude()))
                                            intent.putExtra("type", type)
                                            setResult(RESULT_OK, intent)
                                            finish()
                                        }
                                    })
                                }
                            }
                        }

                        override fun onFailure(call: Call<GeocodingResponse?>?, throwable: Throwable) {
                            throwable.printStackTrace()
                        }
                    })
                }

                else {
                    val adapter = GeolocationAdapter()

                    rvGeo.setHasFixedSize(true)
                    rvGeo.layoutManager = LinearLayoutManager(this@GeolocationActivity)
                    rvGeo.adapter = adapter
                }
            }
            etGeo.hint = hint
            if(text != null) etGeo.setText(text)
            etGeo.requestFocus()

            layoutClickMap.setOnClickListener {
                val intent = Intent()
                intent.putExtra("pick", true)
                intent.putExtra("type", type)
                setResult(RESULT_OK, intent)
                finish()
            }

            layoutUserLocation.setOnClickListener {
                val intent = Intent()
                intent.putExtra("user_location", true)
                intent.putExtra("type", type)
                setResult(RESULT_OK, intent)
                finish()
            }

            if(!isUserLocation) layoutUserLocation.visibility = View.VISIBLE
            else layoutUserLocation.visibility = View.GONE
        }
    }
}