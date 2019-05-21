package com.sayzen.devsupandroidgoogle


import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.sup.dev.java.tools.ToolsThreads

class GoogleNotificationsReceiver : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        ToolsThreads.main { GoogleNotifications.onReceive(remoteMessage) }
    }

}