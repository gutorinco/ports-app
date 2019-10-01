package br.com.suitesistemas.portsmobile.utils

import android.content.Context
import com.google.android.gms.tasks.Task
import com.google.firebase.iid.InstanceIdResult

class FirebaseUtils {

    companion object {

        fun storeToken(task: Task<InstanceIdResult>, context: Context) {
            task.result?.let { result ->
                val firebaseToken = context.getSharedPreferences("firebaseToken", Context.MODE_PRIVATE)
                with (firebaseToken.edit()) {
                    putString("value", result.token)
                    apply()
                    commit()
                }
            }
        }

        fun storeToken(token: String, context: Context) {
            val firebaseToken = context.getSharedPreferences("firebaseToken", Context.MODE_PRIVATE)
            with (firebaseToken.edit()) {
                putString("value", token)
                apply()
                commit()
            }
        }

        fun getToken(context: Context): String {
            val firebaseToken = context.getSharedPreferences("firebaseToken", Context.MODE_PRIVATE)
            return firebaseToken.getString("value", "")!!
        }

    }
}