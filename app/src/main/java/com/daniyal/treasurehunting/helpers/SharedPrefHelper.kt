package com.daniyal.treasurehunting.helpers

import android.content.Context
import android.content.SharedPreferences

class SharedPrefHelper {
    private val MODE = 0

    private fun SharedPrefHelper() {}

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences("", 0)
    }

    private fun getEditor(context: Context): SharedPreferences.Editor {
        return getPreferences(context).edit()
    }

    fun writeInteger(
        context: Context,
        str: String?,
        i: Int
    ) {
        getEditor(context).putInt(str, i).commit()
    }

    fun readInteger(context: Context, str: String?): Int {
        return getPreferences(context).getInt(str, 0)
    }

    fun writeString(
        context: Context,
        str: String?,
        str2: String?
    ) {
        getEditor(context).putString(str, str2).commit()
    }

    fun readString(context: Context, str: String?): String? {
        return getPreferences(context).getString(str, "")
    }

    fun clearAll(context: Context) {
        getEditor(context).clear().commit()
    }

    fun writeBoolean(
        context: Context,
        str: String?,
        z: Boolean
    ) {
        getPreferences(context).edit().putBoolean(str, z).apply()
    }

    fun readBoolean(context: Context, str: String?): Boolean {
        return getPreferences(context).getBoolean(str, false)
    }

}