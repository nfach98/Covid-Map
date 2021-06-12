package com.nfach98.covidmap

import android.graphics.Bitmap
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import com.nfach98.covidmap.api.ApiMain
import com.nfach98.covidmap.api.response.ResponseStatus
import com.nfach98.covidmap.databinding.ActivityChangeProfileBinding
import com.nfach98.covidmap.ui.main.profile.ProfileFragment.Companion.EXTRA_AVATAR
import com.nfach98.covidmap.ui.main.profile.ProfileFragment.Companion.EXTRA_NAME
import com.nfach98.covidmap.ui.main.profile.ProfileFragment.Companion.EXTRA_TOKEN
import com.nfach98.covidmap.ui.main.profile.ProfileFragment.Companion.EXTRA_USERNAME
import com.squareup.picasso.Picasso
import com.vansuita.pickimage.bundle.PickSetup
import com.vansuita.pickimage.dialog.PickImageDialog
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class ChangeProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangeProfileBinding
    private lateinit var file: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val token = intent.getStringExtra(EXTRA_TOKEN)
        val avatar = intent.getStringExtra(EXTRA_AVATAR)
        val name = intent.getStringExtra(EXTRA_NAME)
        val username = intent.getStringExtra(EXTRA_USERNAME)

        val minUsernameLength = 4

        binding.etProfileName.setText(name)
        binding.etProfileUsername.setText(username)
        if (avatar == null)
            Picasso.get()
            .load(R.drawable.drawable_person)
            .into(binding.ivProfile)
        else
            Log.d("avatar", avatar)
            Picasso.get()
            .load("https://covidmapapi.fishmeind.com/$avatar")
            .into(binding.ivProfile)

        binding.etProfileUsername.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Log.d("username", s.toString())
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d("username", s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
                if (token != null && s != null) {
                    enableUpdate(false)

                    if(s.toString().length < minUsernameLength){
                        enableUpdate(false)
                        binding.inputProfileUsername.error = "Username kurang dari $minUsernameLength karakter"
                    }

                    else{
                        binding.inputProfileUsername.error = null
                        binding.loadingUsername.visibility = View.VISIBLE

                        ApiMain().services.checkUsername(token, s.toString()).enqueue(object : Callback<ResponseStatus> {
                            override fun onResponse(call: Call<ResponseStatus>, response: Response<ResponseStatus>) {
                                if(response.code() == 200){
                                    response.body().let {
                                        if(it != null){
                                            binding.loadingUsername.visibility = View.GONE

                                            if(it.error == "unavailable"){
                                                enableUpdate(false)
                                                binding.inputProfileUsername.error = "Username tidak tersedia"
                                            }

                                            else{
                                                enableUpdate(true)
                                                binding.okUsername.visibility = View.VISIBLE
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

        })

        binding.btnProfileAvatar.setOnClickListener {
            PickImageDialog.build(PickSetup())
                .setOnPickResult { result ->
                    Picasso.get().load(result.uri).fit().centerCrop().into(binding.ivProfile)
                    file = File(cacheDir, "avatar.jpg")
                    file.writeBitmap(result.bitmap, Bitmap.CompressFormat.JPEG, 85)
                }
                .show(this)
        }

        binding.btnUpdate.setOnClickListener {
            val nameNew = binding.etProfileName.text.toString()
            val usernameNew = binding.etProfileUsername.text.toString()
            val body = RequestBody.create("image/*".toMediaTypeOrNull(), file)
            val part = MultipartBody.Part.createFormData("avatar", file.name, body)

            if(token != null && nameNew.isNotEmpty() && usernameNew.isNotEmpty()){
                binding.layoutLoadingBlock.visibility = View.VISIBLE
                binding.pulsator.start()

                ApiMain().services.update(token, nameNew, usernameNew, part).enqueue(object : Callback<ResponseStatus> {
                    override fun onResponse(call: Call<ResponseStatus>, response: Response<ResponseStatus>) {
                        if (response.code() == 200) {
                            response.body().let { status ->
                                if(status?.status == "success"){
                                    finish()
                                }
                                else {
                                    binding.layoutLoadingBlock.visibility = View.GONE
                                    binding.pulsator.stop()
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun enableUpdate(enabled: Boolean){
        if(enabled){
            binding.btnUpdate.isEnabled = true
            binding.btnUpdate.setTextColor(resources.getColor(R.color.blue_dark))
            binding.btnUpdate.backgroundTintList = AppCompatResources.getColorStateList(this, R.color.yellow)
        }
        else{
            binding.okUsername.visibility = View.GONE
            binding.btnUpdate.isEnabled = false
            binding.btnUpdate.setTextColor(resources.getColor(android.R.color.black))
            binding.btnUpdate.backgroundTintList = AppCompatResources.getColorStateList(this, android.R.color.darker_gray)
        }
    }

    private fun File.writeBitmap(bitmap: Bitmap, format: Bitmap.CompressFormat, quality: Int) {
        outputStream().use { out ->
            bitmap.compress(format, quality, out)
            out.flush()
        }
    }
}