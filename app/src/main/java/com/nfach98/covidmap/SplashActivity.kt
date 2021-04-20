package com.nfach98.covidmap

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.nfach98.covidmap.api.ApiMain
import com.nfach98.covidmap.api.response.ResponseUser
import com.nfach98.covidmap.databinding.ActivitySplashBinding
import com.nfach98.covidmap.session.UserToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.btnLogin.setOnClickListener {
            val username = binding.etLoginUsername.text.toString()
            val password = binding.etLoginPassword.text.toString()

            ApiMain().services.login(username, password).enqueue(object : Callback<ResponseUser> {
                override fun onResponse(call: Call<ResponseUser>, response: Response<ResponseUser>) {
                    if (response.code() == 200) {
                        response.body().let {
                            if (it != null) {
                                UserToken.setToken(this@SplashActivity, it.token)
                                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                                finish()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseUser>, t: Throwable) {
                    Log.e("API Exception: ", t.toString())
                }
            })
        }

        binding.btnRegister.setOnClickListener {
            val name = binding.etRegisterName.text.toString()
            val username = binding.etRegisterUsername.text.toString()
            val password = binding.etRegisterPassword.text.toString()
            val confirmPassword = binding.etRegisterPasswordConfirm.text.toString()

            ApiMain().services.register(name, username, password, confirmPassword).enqueue(object : Callback<ResponseUser> {
                override fun onResponse(call: Call<ResponseUser>, response: Response<ResponseUser>) {
                    if (response.code() == 200) {
                        response.body().let {
                            if (it != null) {
                                UserToken.setToken(this@SplashActivity, it.token)
                                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                                finish()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseUser>, t: Throwable) {
                    Log.e("API Exception: ", t.toString())
                }
            })
        }

        Handler(Looper.getMainLooper()).postDelayed({
            val isLogin = UserToken.getToken(this@SplashActivity) != null
            if(isLogin) {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                finish()
            }
        }, 1750)
    }
}