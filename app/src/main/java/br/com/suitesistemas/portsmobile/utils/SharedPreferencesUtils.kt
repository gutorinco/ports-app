package br.com.suitesistemas.portsmobile.utils

import android.app.Activity
import android.content.Context

class SharedPreferencesUtils {

    companion object {
        fun getCompanyName(activity: Activity): String {
            val sharedPref = activity.getSharedPreferences("userResponse", Context.MODE_PRIVATE)
            sharedPref?.let { pref ->
                return pref.getString("empresa", "")!!
            }
            return ""
        }
    }
}