package com.csci5708.dalcommunity.firestore

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.csci5708.dalcommunity.activity.AnswerBroadcastQuestionActivity
import com.csci5708.dalcommunity.activity.HomeActivity
import com.example.dalcommunity.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MyFirebaseMessagingService : FirebaseMessagingService() {
    var onMsgReceivedCalled = false
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        onMsgReceivedCalled = true
        remoteMessage.notification?.let {
            if (it.title!!.contentEquals("Help a peer out!")) {
                sendBroadcastNotification(it.title, it.body!!)
                Log.d("NOTIFICATION","BROADCAST")
            } else {
                sendNotification(it.title, it.body)
                Log.d("NOTIFICATION","NORMAL")
            }
        }
    }

    override fun handleIntent(intent: Intent?) {
        if (!onMsgReceivedCalled) {
            val bundle = intent?.extras
            val dataReceived: MutableMap<String, String?> = HashMap()
            if (bundle != null) {
                for (key in bundle.keySet()) {
                    val value = bundle[key]
                    dataReceived[key] = value.toString()
                }
                val title = dataReceived["title"]
                val msg = dataReceived["message"]
                Log.d("DATARECEIVED", dataReceived.toString())
                Log.d("DATARECEIVED_TITLE", title.toString())
                Log.d("DATARECEIVED_MSG", msg.toString())
                sendBroadcastNotification(title, msg.toString())
                onMsgReceivedCalled = false
            }
        } else {
            super.handleIntent(intent)
        }
    }


    private fun sendNotification(title: String?, messageBody: String?) {
        val intent = Intent(this, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

        val channelId = "dalCommunityNotification"
        val defaultSoundUri = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.home)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Channel human readable title", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(0, notificationBuilder.build())
    }

    private fun sendBroadcastNotification(title: String?, messageBody: String) {
        try {
            val userEmail = messageBody?.substring(messageBody.indexOf("¨")+1,messageBody.indexOf("∫"))
            val broadcastId = messageBody?.substring(messageBody.indexOf("∫")+1,messageBody.indexOf("µ"))
            val message = messageBody?.substring(messageBody.indexOf("µ")+1,messageBody.length)
            val intent = Intent(this, AnswerBroadcastQuestionActivity::class.java)
            intent.putExtra("userEmail", userEmail)
            intent.putExtra("broadcastId", broadcastId)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            val pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

            val channelId = "dalCommunityNotification"
            val defaultSoundUri = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI

            val notificationBuilder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.home)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(channelId, "Channel human readable title", NotificationManager.IMPORTANCE_DEFAULT)
                notificationManager.createNotificationChannel(channel)
            }
            notificationManager.notify(0, notificationBuilder.build())
        } catch (e: Exception) {
            Log.d("",e.message.toString())
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }
}