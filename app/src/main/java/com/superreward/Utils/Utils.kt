package com.nkomapp.rupeequiz.Utils

import android.content.Context
import androidx.appcompat.app.AppCompatActivity


object Utils {
    fun saveData(context: Context,key:String,value:String){
        val sharedpref=context.getSharedPreferences("mySharedPref",Context.MODE_PRIVATE)
        val editor=sharedpref.edit()
        editor.putString(key,value)
        editor.apply()
    }
    fun getData(context: Context,key: String):String?{
        val sharedpref=context.getSharedPreferences("mySharedPref",Context.MODE_PRIVATE)
        return sharedpref.getString(key,null)

    }

    fun Context.saveUserDetails(name: String, phone: String) {
        val sharedPreferences = getSharedPreferences("userDataDB", AppCompatActivity.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("name", name)
        editor.putString("phone", phone)
        editor.apply()
    }


    fun Context.addDepositAmount(deposit: Int) {
        val sharedPreferences = getSharedPreferences("userDataDB", AppCompatActivity.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("deposit", deposit)
        editor.apply()
    }


    fun Context.addWinningAmount(winning: Int) {
        val sharedPreferences = getSharedPreferences("userDataDB", AppCompatActivity.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("winning", winning)
        editor.apply()
    }


    fun Context.getDepositAmount(): Int {
        val sharedPreferences = getSharedPreferences("userDataDB", AppCompatActivity.MODE_PRIVATE)
        return sharedPreferences.getInt("deposit", 0)

    }


    fun Context.getWinningAmount(): Int {
        val sharedPreferences = getSharedPreferences("userDataDB", AppCompatActivity.MODE_PRIVATE)
        return sharedPreferences.getInt("winning", 0)

    }
}