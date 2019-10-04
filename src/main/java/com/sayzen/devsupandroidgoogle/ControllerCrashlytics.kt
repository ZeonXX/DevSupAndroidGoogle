package com.sayzen.devsupandroidgoogle

import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.CustomEvent


object ControllerCrashlytics{

    fun sendError(text: String) {
        Answers.getInstance().logCustom(
                CustomEvent("Error")
                        .putCustomAttribute("Error message", text)
        )
    }
}