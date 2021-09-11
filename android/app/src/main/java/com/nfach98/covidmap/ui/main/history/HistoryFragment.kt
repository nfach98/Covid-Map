package com.nfach98.covidmap.ui.main.history

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.mapbox.geojson.FeatureCollection
import com.nfach98.covidmap.R
import com.nfach98.covidmap.api.ApiMain
import com.nfach98.covidmap.databinding.FragmentHistoryBinding
import com.nfach98.covidmap.databinding.FragmentMapBinding
import com.nfach98.covidmap.model.History
import com.nfach98.covidmap.session.UserToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HistoryFragment : Fragment() {

    private lateinit var binding: FragmentHistoryBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentHistoryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val token = UserToken.getToken(requireActivity().applicationContext)
        if (token != null) {
            with(binding) {
                ApiMain().services.getHistory(token).enqueue(object : Callback<ArrayList<History>> {
                    override fun onResponse(call: Call<ArrayList<History>>, response: Response<ArrayList<History>>) {
                        if (response.code() == 200) {
                            response.body().let {
                                if (it != null) {
                                    val adapter = HistoryAdapter()
                                    adapter.histories = it

                                    rvHistory.setHasFixedSize(true)
                                    rvHistory.layoutManager = LinearLayoutManager(requireContext())
                                    loading.visibility = View.GONE
                                    rvHistory.adapter = adapter
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<ArrayList<History>>, t: Throwable) {
                        Log.e("API Exception: ", t.toString())
                    }
                })
            }
        }
    }
}