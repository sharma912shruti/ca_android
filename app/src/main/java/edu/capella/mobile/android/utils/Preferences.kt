package edu.capella.mobile.android.utils

import android.content.Context
import android.content.SharedPreferences


/**
 * Preferences : Class provides static methods for setting or getting values
 * in  Shared ppreference
 *
 * @author  :  Kush.pandya
 * @version :  1.0
 * @since   :  14-02-2020
 *
 */

object Preferences {

    private val prefName = "capella"
    private var sharedPreferences: SharedPreferences? = null

//    This method is used to create instance of sharedPreferences
    fun getInstance(context: Context) {
        sharedPreferences = context.getSharedPreferences(
            prefName, Context.MODE_PRIVATE)
    }

//    This method is used to store string value in sharedPreferences

    fun addValue(preferencesKey: PreferenceKeys, value: String) {
        val  editor = sharedPreferences!!.edit()
        editor.putString(preferencesKey.toString(), value)
        editor.apply()
    }
    //    This method is used to store boolean value in sharedPreferences

    fun addBoolean(preferencesKey: PreferenceKeys, value: Boolean) {
        val editor = sharedPreferences!!.edit()
        editor.putBoolean(preferencesKey.toString(), value)
        editor.apply()
    }

    //    This method is used to store boolean value in sharedPreferences

    fun addArrayList(preferencesKey: String, value: String) {
        val editor = sharedPreferences!!.edit()
        editor.putString(preferencesKey, value)
        editor.apply()
    }

    fun getArrayList(key: String): String? {
        return sharedPreferences!!.getString(key, "")
    }

    //    This method is used to store integer  value in sharedPreferences

    fun addInt(preferencesKey: PreferenceKeys, value: Int) {
        val editor = sharedPreferences!!.edit()
        editor.putInt(preferencesKey.toString(), value)
        editor.apply()
    }

    //    This method is used to get string  value from sharedPreferences

    fun getValue(key: PreferenceKeys): String? {
        return sharedPreferences!!.getString(key.toString(), "")
    }

    //    This method is used to get integer  value from sharedPreferences
    fun getInt(key: PreferenceKeys): Int {
        return sharedPreferences!!.getInt(key.toString(), 0)
    }

    //    This method is used to get boolean  value from sharedPreferences
    fun getBoolean(key: PreferenceKeys): Boolean {
        return sharedPreferences!!.getBoolean(key.toString(), false)
    }

    //    clear sharedPreferences
    fun clearSharedPreference() {
        val editor = sharedPreferences!!.edit()
        editor.clear()
        editor.apply()
    }

    fun removeKey(keyName: String)
    {
        val editor = sharedPreferences!!.edit()
        editor.remove(keyName)
        editor.apply()
    }
}
