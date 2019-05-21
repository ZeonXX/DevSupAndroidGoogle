package com.sayzen.devsupandroidgoogle

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.sup.dev.java.libs.api_simple.client.TokenProvider

object ControllerToken {

    private var googleAccount: GoogleSignInAccount? = null
    private var onLoginFailed: ()->Unit = {}

    fun init(onLoginFailed: ()->Unit){
        this.onLoginFailed = onLoginFailed
    }

    fun getGooglePhotoUrl():String?{
        if(googleAccount == null || googleAccount!!.photoUrl == null) return null
        return  googleAccount!!.photoUrl!!.toString()
    }

    fun instanceTokenProvider(): TokenProvider {
        return object : TokenProvider {

            override fun getToken(callbackSource: (String?)->Unit) {
                ControllerToken.getToken(callbackSource)
            }

            override fun clearToken() {
                ControllerToken.clearToken()
            }

            override fun onLoginFailed() {
                ControllerToken.onLoginFailed()
            }
        }
    }

    fun logout(callback: (()->Unit)) {
        googleAccount = null
        GoogleAuth.exit(callback)
    }

    fun getToken(onResult: (String?)->Unit) {
        if (googleAccount != null) {
            onResult.invoke(googleAccount!!.idToken)
            return
        }
        GoogleAuth.getGoogleToken { googleAccount ->
            ControllerToken.googleAccount = googleAccount
            onResult.invoke(googleAccount?.idToken)
        }
    }

    fun clearToken() {
        googleAccount = null
    }

    fun onLoginFailed() {
        onLoginFailed.invoke()
    }

    fun containsToken(): Boolean {
        return googleAccount != null
    }

}