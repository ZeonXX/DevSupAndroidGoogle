package com.sayzen.devsupandroidgoogle

import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.sup.dev.java.libs.debug.log
import com.sup.dev.java.tools.ToolsThreads

class GoogleNotifications : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        ToolsThreads.main { onReceive(remoteMessage) }
    }

    override fun onNewToken(s: String?) {
        super.onNewToken(s)
        if (onToken != null) onToken!!.invoke(s)
    }

    companion object {

        private var onToken: ((String?) -> Unit)? = null
        private var onReceive: ((RemoteMessage) -> Unit)? = null

        fun init(onToken: ((String?) -> Unit)?, onReceive: ((RemoteMessage) -> Unit)?) {
            log("GoogleNotifications", "init")
            GoogleNotifications.onToken = onToken
            GoogleNotifications.onReceive = onReceive

            val fastToken = FirebaseInstanceId.getInstance().token
            log("GoogleNotifications", "fastToken [$fastToken]")
            if(fastToken != null) onToken?.invoke(fastToken)
            else {
                FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener { instanceIdResult ->
                    log("GoogleNotifications", "getInstance")
                    onToken?.invoke(instanceIdResult.token)
                }
            }
        }

        internal fun onReceive(remoteMessage: RemoteMessage) {
            onReceive!!.invoke(remoteMessage)
        }
    }

}