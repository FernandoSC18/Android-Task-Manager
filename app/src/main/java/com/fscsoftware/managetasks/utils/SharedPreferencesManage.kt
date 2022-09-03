package com.fscsoftware.managetasks.utils

import android.app.Activity
import android.content.Context
import com.fscsoftware.managetasks.R

object SharedPreferencesManage {

    fun getSharedPref (activity: Activity) = activity.getPreferences(Context.MODE_PRIVATE) ?: null

    fun booleanValueOrDefValue (context : Context, name : String, def : Boolean) : Boolean {
        val sharedPref = getSharedPref(context as Activity)
        val preferenceValue : Boolean? = sharedPref?.getBoolean(name, def)
        return preferenceValue ?: def
    }

    fun getOrientation (context: Context): Boolean {
        val prefNameOrientation = context.resources.getString(R.string.preference_name_orientation)
        val valDefOrientation = context.resources.getBoolean(R.bool.preference_default_orientation)

        return booleanValueOrDefValue (context, prefNameOrientation, valDefOrientation)
    }

    fun setOrientation (context: Context, bool : Boolean) {
        val prefNameOrientation = context.resources.getString(R.string.preference_name_orientation)

        val sharedPref = getSharedPref(context as Activity)
        with (sharedPref?.edit()) {
            this?.putBoolean(prefNameOrientation, bool)
            this?.apply()
        }
    }

    fun getSwipeFinish (context: Context): Boolean {
        val prefNameSwiftFinish = context.resources.getString(R.string.preference_name_swipe_finish)
        val valDefSwiftFinish = context.resources.getBoolean(R.bool.preference_default_swipe_finish)

        return booleanValueOrDefValue (context, prefNameSwiftFinish, valDefSwiftFinish)
    }

    fun setSwipeFinish (context: Context, bool : Boolean) {
        val prefNameSwiftFinish = context.resources.getString(R.string.preference_name_swipe_finish)

        val sharedPref = getSharedPref(context as Activity)
        with (sharedPref?.edit()) {
            this?.putBoolean(prefNameSwiftFinish, bool)
            this?.apply()
        }
    }

    fun getSwipeDelete(context: Context): Boolean {
        val prefNameSwiftDelete = context.resources.getString(R.string.preference_name_swipe_delete)
        val valDefSwiftDelete = context.resources.getBoolean(R.bool.preference_default_swipe_delete)

        return booleanValueOrDefValue (context, prefNameSwiftDelete, valDefSwiftDelete)
    }

    fun setSwipeDelete (context: Context, bool : Boolean) {
        val prefNameSwiftDelete = context.resources.getString(R.string.preference_name_swipe_delete)

        val sharedPref = getSharedPref(context as Activity)
        with (sharedPref?.edit()) {
            this?.putBoolean(prefNameSwiftDelete, bool)
            this?.apply()
        }
    }

}