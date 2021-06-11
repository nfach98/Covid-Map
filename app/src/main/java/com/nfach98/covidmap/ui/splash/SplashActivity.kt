package com.nfach98.covidmap.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.nfach98.covidmap.api.ApiMain
import com.nfach98.covidmap.api.response.ResponseUser
import com.nfach98.covidmap.databinding.ActivitySplashBinding
import com.nfach98.covidmap.session.UserToken
import com.nfach98.covidmap.ui.main.MainActivity
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
            hideKeyboard()
            loading(true)

            val username = binding.etLoginUsername.text.toString()
            val password = binding.etLoginPassword.text.toString()

            ApiMain().services.login(username, password).enqueue(object : Callback<ResponseUser> {
                override fun onResponse(call: Call<ResponseUser>, response: Response<ResponseUser>) {
                    if (response.code() == 200) {
                        response.body().let {
                            if (it != null) {
                                UserToken.setToken(applicationContext, it.token)
//                                binding.etLoginUsername.setText("")
//                                binding.etLoginPassword.setText("")
//                                binding.layoutLoadingBlock.visibility = View.GONE
                                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                                finish()
                            }
                        }
                    }
                    else {
                        loading(false)
                    }
                }

                override fun onFailure(call: Call<ResponseUser>, t: Throwable) {
                    loading(false)
                    Log.e("API Exception: ", t.toString())
                }
            })
        }

        binding.btnRegister.setOnClickListener {
            hideKeyboard()
            loading(true)

            val name = binding.etRegisterName.text.toString()
            val username = binding.etRegisterUsername.text.toString()
            val password = binding.etRegisterPassword.text.toString()
            val confirmPassword = binding.etRegisterPasswordConfirm.text.toString()

            ApiMain().services.register(name, username, password, confirmPassword).enqueue(object : Callback<ResponseUser> {
                override fun onResponse(call: Call<ResponseUser>, response: Response<ResponseUser>) {
                    if (response.code() == 200) {
                        response.body().let {
                            if (it != null) {
                                UserToken.setToken(applicationContext, it.token)
//                                binding.etRegisterName.setText("")
//                                binding.etRegisterUsername.setText("")
//                                binding.etRegisterPassword.setText("")
//                                binding.etRegisterPasswordConfirm.setText("")
//                                binding.layoutLoadingBlock.visibility = View.GONE
                                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                                finish()
                            }
                        }
                    }
                    else {
                        loading(false)
                    }
                }

                override fun onFailure(call: Call<ResponseUser>, t: Throwable) {
                    loading(false)
                    Log.e("API Exception: ", t.toString())
                }
            })
        }

        Handler(Looper.getMainLooper()).postDelayed({
            val isLogin = UserToken.getToken(applicationContext) != null
            if (isLogin) {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            }
        }, 1300)
    }

    fun loading(show: Boolean){
        if(show){
            binding.pulsator.start()
            binding.layoutLoadingBlock.visibility = View.VISIBLE
        }
        else{
            binding.layoutLoadingBlock.visibility = View.GONE
            binding.pulsator.stop()
        }
    }

    private fun hideKeyboard() {
        val imm = this@SplashActivity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        var view = this@SplashActivity.currentFocus
        if (view == null) {
            view = View(this@SplashActivity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}