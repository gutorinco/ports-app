package br.com.suitesistemas.portsmobile.firebase

import android.app.ActivityManager
import android.app.KeyguardManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import br.com.suitesistemas.portsmobile.R
import br.com.suitesistemas.portsmobile.utils.FirebaseUtils
import br.com.suitesistemas.portsmobile.view.activity.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MessagingService : FirebaseMessagingService() {

    private lateinit var localBroadcast: LocalBroadcastManager

    override fun onCreate() {
        localBroadcast = LocalBroadcastManager.getInstance(applicationContext)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("ON MESSAGE RECEIVED:", "From: ${remoteMessage.from}")

        remoteMessage.notification?.let {
            if (shouldShowNotification()) {
                val intent = getIntent(it.clickAction!!)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

                val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

                val notification = NotificationCompat.Builder(this, "Ports")
                    .setSmallIcon(R.drawable.logo)
                    .setLargeIcon(BitmapFactory.decodeResource(applicationContext.resources, R.drawable.logo))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentTitle(it.title)
                    .setContentText(it.body)
                    .setContentIntent(pendingIntent)
                with (NotificationManagerCompat.from(this)) {
                    notify(1, notification.build())
                }
            } else {
                if (!remoteMessage.data.isNullOrEmpty()) {
                    val receivedToken = remoteMessage.data["token"]
                    val firebaseToken = FirebaseUtils.getToken(this)
                    if (receivedToken != firebaseToken)
                        localBroadcast.sendBroadcast(Intent(it.clickAction!!))
                }
            }
        }
    }

    override fun onNewToken(token: String) {
        FirebaseUtils.storeToken(token, this)
    }

    private fun getIntent(clickAction: String): Intent {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(
            "notificationToFragment", when (clickAction) {
                "br.com.suitesistemas.portsmobile_TARGET_CLIENTE" -> "cliente"
                "br.com.suitesistemas.portsmobile_TARGET_PEDIDO" -> "pedido"
                "br.com.suitesistemas.portsmobile_TARGET_TAREFA" -> "tarefa"
                "br.com.suitesistemas.portsmobile_TARGET_VENDA" -> "venda"
                else -> "cliente"
            }
        )
        return intent
    }

    private fun shouldShowNotification(): Boolean {
        val process = ActivityManager.RunningAppProcessInfo()
        ActivityManager.getMyMemoryState(process)
        if (process.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND)
            return true
        val keyguardManager = applicationContext.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        return keyguardManager.inKeyguardRestrictedInputMode()
    }

}