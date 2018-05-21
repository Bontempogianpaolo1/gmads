package gmads.it.gmads_lab1.service;

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import gmads.it.gmads_lab1.R
import gmads.it.gmads_lab1.Home
import gmads.it.gmads_lab1.util.MyNotificationManager


class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.notification != null) {
            notifyUser(remoteMessage.from as String, remoteMessage.notification?.body as String)

            Log.d("FCM", "FCM message received!")

        }
        sendNotification(remoteMessage)
    }

    fun notifyUser(from : String, notification : String){
        var myNotificationManager = MyNotificationManager(this)
        var intent = Intent(this, Home::class.java)

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        myNotificationManager.showNotification(from, notification, intent)
    }
    private fun sendNotification(remoteMessage: RemoteMessage) {
        val intent = Intent(this, Home::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this,"")
                .setContentText(remoteMessage.notification?.body as String)
                .setContentTitle(remoteMessage.from as String)
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }
}