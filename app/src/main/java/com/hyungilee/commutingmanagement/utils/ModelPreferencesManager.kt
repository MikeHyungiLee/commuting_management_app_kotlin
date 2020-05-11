package com.hyungilee.commutingmanagement.utils

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.GsonBuilder
import com.google.gson.internal.GsonBuildConfig
import com.hyungilee.commutingmanagement.utils.Constants.PREFERENCES_FILE_NAME

object ModelPreferencesManager {
    lateinit var preferences: SharedPreferences

    //SharedPreferenceから保存されたデータを呼び出す時とデータを保存する時に一応このメソットを呼び出します。
    fun with(application: Application){
        preferences = application.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)
    }

    // Preferenceにデータを保存するメソット
    /**
     *@param `object` Object of model class (of type [T]) to save
     *@param key Key with which Shared preferences to
     */
    fun <T> putJsonToString(value :T, key: String){
        // ObjectをJSON Stringに変更します。
        val jsonString = GsonBuilder().create().toJson(value)
        // String値をSharedPreferenceに保存する
        preferences.edit().putString(key, jsonString).apply()
    }

    inline fun <reified T>getJsonObjectFromJsonString(key: String): T?{
        // JSON String値を読む
        val value = preferences.getString(key, null)
        // JSON String値をModel Objectに変更して返却
        return GsonBuilder().create().fromJson(value, T::class.java)
    }

    // String値をSharedPreferenceに保存するメソット
    fun putStringVal(value: String, key:String){
        preferences.edit().putString(key, value).apply()
    }

    // ShardPreferenceに保存されているString値を取得するメソット
    fun getStringVal(key: String): String?{
        return preferences.getString(key, null)
    }

    // Int値をSharedPreferenceに保存するメソット
    fun putIntVal(value: Int, key:String){
        preferences.edit().putInt(key, value).apply()
    }

    // ShardPreferenceに保存されているInt値を取得するメソット
    fun getIntVal(key: String): Int?{
        return preferences.getInt(key, 0)
    }

}