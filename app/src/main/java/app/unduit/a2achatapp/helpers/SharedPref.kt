package app.unduit.a2achatapp.helpers

import android.content.Context


object SharedPref {

    private const val sharedPrefKey = "a2a_chat"



    @JvmStatic
    fun setBoolean(context: Context, key: String, value: Boolean) {
        val editor = context.getSharedPreferences(sharedPrefKey, Context.MODE_PRIVATE).edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    @JvmStatic
    fun getBoolean(context: Context, key: String, defaultValue: Boolean): Boolean {
        val sharedPreference = context.getSharedPreferences(sharedPrefKey, Context.MODE_PRIVATE)
        return sharedPreference.getBoolean(key, defaultValue)
    }

    fun setInt(context: Context, key: String, value: Int) {
        val editor = context.getSharedPreferences(sharedPrefKey, Context.MODE_PRIVATE).edit()
        editor.putInt(key, value)
        editor.apply()
    }

    @JvmStatic
    fun getInt(context: Context, key: String, defaultValue: Int): Int {
        val sharedPreference = context.getSharedPreferences(sharedPrefKey, Context.MODE_PRIVATE)
        return sharedPreference.getInt(key, defaultValue)
    }

    fun setFloat(context: Context, key: String, value: Float) {
        val editor = context.getSharedPreferences(sharedPrefKey, Context.MODE_PRIVATE).edit()
        editor.putFloat(key, value)
        editor.apply()
    }

    fun getFloat(context: Context, key: String, defaultValue: Float): Float {
        val sharedPreference = context.getSharedPreferences(sharedPrefKey, Context.MODE_PRIVATE)
        return sharedPreference.getFloat(key, defaultValue)
    }

    @JvmStatic
    fun setString(context: Context, key: String, value: String?) {
        val editor = context.getSharedPreferences(sharedPrefKey, Context.MODE_PRIVATE).edit()
        editor.putString(key, value)
        editor.apply()
    }

    @JvmStatic
    fun getString(context: Context, key: String, defaultValue: String): String {
        val sharedPreference = context.getSharedPreferences(sharedPrefKey, Context.MODE_PRIVATE)
        return sharedPreference.getString(key, defaultValue)!!
    }

    fun remove(context: Context, key: String) {
        val editor = context.getSharedPreferences(sharedPrefKey, Context.MODE_PRIVATE).edit()
        editor.remove(key)
        editor.apply()
    }

    @JvmStatic
    fun clearAllData(context: Context) {
        val editor = context.getSharedPreferences(sharedPrefKey, Context.MODE_PRIVATE).edit()
        editor.clear()
        editor.apply()
    }


}