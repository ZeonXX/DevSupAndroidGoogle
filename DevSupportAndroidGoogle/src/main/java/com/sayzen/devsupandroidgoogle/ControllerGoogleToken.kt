package com.sayzen.devsupandroidgoogle

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.sup.dev.java.libs.api_simple.client.TokenProvider

object ControllerGoogleToken {

    var tokenPostExecutor:(String?, callback:(String?)->Unit)->Unit={token,callback -> callback.invoke(token)}
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
                ControllerGoogleToken.getToken{
                    tokenPostExecutor.invoke(it){ token->
                        callbackSource.invoke(token)
                    }
                }
            }

            override fun clearToken() {
                ControllerGoogleToken.clearToken()
            }

            override fun onLoginFailed() {
                ControllerGoogleToken.onLoginFailed()
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
            ControllerGoogleToken.googleAccount = googleAccount
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