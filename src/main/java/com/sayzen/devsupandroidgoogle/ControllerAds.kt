package com.sayzen.devsupandroidgoogle

object ControllerAds {

    val DEBUG_AD = "ca-app-pub-3940256099942544/2247696110"

    var isDebug = false
    var key_pub = ""
    var key_app = ""


    fun init(isDebug: Boolean, key_pub:String, key_project: String) {
        this.isDebug = isDebug
        this.key_pub = key_pub
        this.key_app = if (isDebug) "" else key_project
    }

}