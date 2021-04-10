package com.nfach98.covidmap

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.constraintlayout.widget.ConstraintSet
import com.nfach98.covidmap.databinding.ActivityMainBinding
import com.nfach98.covidmap.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        Handler(Looper.getMainLooper()).postDelayed({
            val set = ConstraintSet()
            set.clone(binding.layoutSplash)
            set.clear(R.id.tv_logo, ConstraintSet.BOTTOM)
            set.applyTo(binding.layoutSplash)

            binding.ivLogo.visibility = View.GONE
//            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
//            finish()
        }, 3000)
    }
}