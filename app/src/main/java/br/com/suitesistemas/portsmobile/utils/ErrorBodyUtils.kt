package br.com.suitesistemas.portsmobile.utils

import okhttp3.ResponseBody
import org.json.JSONObject

class ErrorBodyUtils {

    companion object {

        fun handler(errorBody: ResponseBody?): String {
            return try {
                val body = JSONObject(errorBody?.string())
                body.getString("message")
            } catch (ex: Exception) {
                ""
            }
        }

    }

}