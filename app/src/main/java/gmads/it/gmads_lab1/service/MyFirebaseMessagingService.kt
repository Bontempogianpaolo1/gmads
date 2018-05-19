package gmads.it.gmads_lab1.service;

import android.content.Intent
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import gmads.it.gmads_lab1.Home
import gmads.it.gmads_lab1.util.MyNotificationManager


class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.notification != null) {
            notifyUser(remoteMessage.from as String, remoteMessage.notification?.body as String)

            Log.d("FCM", "FCM message received!")
        }
    }

    fun notifyUser(from : String, notification : String){
        var myNotificationManager = MyNotificationManager(this)
        var intent = Intent(this, Home::class.java)

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        myNotificationManager.showNotification(from, notification, intent)
    }
}