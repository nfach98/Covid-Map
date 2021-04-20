package com.nfach98.covidmap.session

import android.content.Context
import androidx.appcompat.app.AppCompatActivity

class UserToken {
    companion object{
        fun setToken(context: Context, token: String){
            val sp = context.getSharedPreferences("CovidMap", AppCompatActivity.MODE_PRIVATE)
            val editor = sp.edit()
            editor.putString("token", token)
            editor.apply()
        }

        fun getToken(context: Context): String? {
            val sp = context.getSharedPreferences("CovidMap", AppCompatActivity.MODE_PRIVATE)
            return sp.getString("token", null)
        }

        fun removeToken(context: Context) {
            val sp = context.getSharedPreferences("CovidMap", AppCompatActivity.MODE_PRIVATE)
            val editor = sp.edit()
            editor.remove("token")
            editor.apply()
        }
    }
}