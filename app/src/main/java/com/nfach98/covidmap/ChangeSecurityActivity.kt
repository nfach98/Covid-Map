package com.nfach98.covidmap

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.nfach98.covidmap.databinding.ActivityChangeProfileBinding
import com.nfach98.covidmap.databinding.ActivityChangeSecurityBinding

class ChangeSecurityActivity : AppCompatActivity() {

    lateinit var binding: ActivityChangeSecurityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeSecurityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}