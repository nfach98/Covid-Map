package com.nfach98.covidmap.ui.main.profile

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.nfach98.covidmap.ChangeProfileActivity
import com.nfach98.covidmap.ChangeSecurityActivity
import com.nfach98.covidmap.R
import com.nfach98.covidmap.api.ApiMain
import com.nfach98.covidmap.api.response.ResponseStatus
import com.nfach98.covidmap.api.response.ResponseUser
import com.nfach98.covidmap.databinding.FragmentProfileBinding
import com.nfach98.covidmap.session.UserToken
import com.nfach98.covidmap.ui.main.MainActivity
import com.nfach98.covidmap.ui.splash.SplashActivity
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ProfileFragment : Fragment() {

    companion object{
        const val EXTRA_AVATAR = "extra_avatar"
        const val EXTRA_NAME = "extra_name"
        const val EXTRA_USERNAME = "extra_username"
        const val EXTRA_TOKEN = "extra_token"
    }

    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentProfileBinding.inflate(inflater)
        binding.layoutProfile.visibility = View.GONE
        binding.loading.visibility = View.VISIBLE

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val token = UserToken.getToken(requireActivity().applicationContext)

        if (token != null) {
            ApiMain().services.detail(token).enqueue(object : Callback<ResponseUser> {
                override fun onResponse(
                    call: Call<ResponseUser>,
                    response: Response<ResponseUser>
                ) {
                    if (response.code() == 200) {
                        response.body().let { user ->
                            if (user != null) {
                                binding.loading.visibility = View.GONE
                                binding.layoutProfile.visibility = View.VISIBLE

                                binding.tvName.text = user.name
                                binding.tvUsername.text = user.username
                                if (user.avatar == null) Picasso.get()
                                    .load(R.drawable.drawable_person).into(
                                    binding.ivProfile
                                )
                                else Picasso.get().load(user.avatar).into(binding.ivProfile)

                                binding.tvChangeProfile.setOnClickListener {
                                    val intent = Intent(requireContext(), ChangeProfileActivity::class.java)
                                    intent.putExtra(EXTRA_TOKEN, token)
                                    intent.putExtra(EXTRA_AVATAR, user.avatar)
                                    intent.putExtra(EXTRA_NAME, user.name)
                                    intent.putExtra(EXTRA_USERNAME, user.username)
                                    startActivity(intent)
                                }

                                binding.tvSecurity.setOnClickListener {
                                    startActivity(Intent(requireContext(), ChangeSecurityActivity::class.java))
                                }

                                binding.tvLogout.setOnClickListener {
                                    val dialog = Dialog(requireActivity())
                                    dialog.setContentView(R.layout.dialog_logout)

                                    val tvCancel = dialog.findViewById<TextView>(R.id.tv_cancel)
                                    tvCancel.setOnClickListener {
                                        dialog.dismiss()
                                    }

                                    val tvLogout = dialog.findViewById<TextView>(R.id.tv_logout)
                                    tvLogout.setOnClickListener {
                                        dialog.dismiss()
                                        (activity as MainActivity).binding.pulsator.start()
                                        (activity as MainActivity).binding.layoutLoadingBlock.visibility = View.VISIBLE

                                        ApiMain().services.logout(token).enqueue(object : Callback<ResponseStatus> {
                                            override fun onResponse(call: Call<ResponseStatus>, response: Response<ResponseStatus>) {
                                                if (response.code() == 200) {
                                                    response.body().let {
                                                        if (it != null) {
                                                            activity?.applicationContext?.let { c ->
                                                                UserToken.removeToken(c)
                                                            }
                                                            (activity as MainActivity).binding.pulsator.stop()
                                                            (activity as MainActivity).binding.layoutLoadingBlock.visibility =
                                                                    View.GONE
                                                            activity?.finish()

                                                            startActivity(Intent(requireContext(), SplashActivity::class.java))
                                                        }
                                                    }
                                                }
                                            }

                                            override fun onFailure(call: Call<ResponseStatus>, t: Throwable) {
                                                Log.e("API Exception: ", t.toString())
                                            }
                                        })
                                    }

                                    dialog.show()
                                }
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseUser>, t: Throwable) {
                    Log.e("API Exception: ", t.toString())
                }
            })
        }
    }
}