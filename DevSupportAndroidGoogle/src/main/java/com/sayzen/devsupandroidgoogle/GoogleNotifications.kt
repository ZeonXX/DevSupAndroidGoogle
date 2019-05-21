package com.sayzen.devsupandroidgoogle

import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class GoogleNotifications : FirebaseMessagingService() {

    override fun onNewToken(s: String?) {
        super.onNewToken(s)
        if (onToken != null) onToken!!.invoke(s)
    }

    companion object {

        private var onToken: ((String?) -> Unit)? = null
        private var onReceive: ((RemoteMessage) -> Unit)? = null

        fun init(onToken: ((String?) -> Unit)?, onReceive: ((RemoteMessage) -> Unit)?) {
            GoogleNotifications.onToken = onToken
            GoogleNotifications.onReceive = onReceive

            val token = FirebaseInstanceId.getInstance().token
            onToken?.invoke(token)
        }

        internal fun onReceive(remoteMessage: RemoteMessage) {
            onReceive!!.invoke(remoteMessage)
        }
    }

}