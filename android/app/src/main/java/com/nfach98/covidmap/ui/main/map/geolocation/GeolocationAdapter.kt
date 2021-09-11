package com.nfach98.covidmap.ui.main.map.geolocation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mapbox.api.geocoding.v5.models.CarmenFeature
import com.nfach98.covidmap.databinding.ItemGeolocationBinding
import com.nfach98.covidmap.ui.main.history.HistoryAdapter

class GeolocationAdapter : RecyclerView.Adapter<GeolocationAdapter.GeolocationViewHolder>() {

    var features = ArrayList<CarmenFeature>()

    private var onItemActionCallback: OnItemActionCallback? = null

    fun setOnItemClickCallback(onItemActionCallback: OnItemActionCallback) {
        this.onItemActionCallback = onItemActionCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GeolocationViewHolder {
        val view = ItemGeolocationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GeolocationViewHolder(view)
    }

    override fun onBindViewHolder(holder: GeolocationViewHolder, position: Int) {
        holder.bind(features[position])
    }

    override fun getItemCount(): Int {
        return features.size
    }

    inner class GeolocationViewHolder(private val binding: ItemGeolocationBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(feature: CarmenFeature){
            itemView.setOnClickListener { onItemActionCallback?.onItemClicked(feature) }

            with(binding){
                tvName.text = feature.text()
                tvPlace.text = feature.placeName()
            }
        }
    }

    interface OnItemActionCallback {
        fun onItemClicked(data: CarmenFeature)
    }
}