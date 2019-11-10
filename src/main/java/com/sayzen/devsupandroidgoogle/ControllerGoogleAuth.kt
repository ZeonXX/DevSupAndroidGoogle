package com.sayzen.devsupandroidgoogle

import android.view.Gravity
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.sup.dev.android.app.SupAndroid
import com.sup.dev.android.tools.ToolsAndroid
import com.sup.dev.android.tools.ToolsIntent
import com.sup.dev.java.libs.api_simple.client.TokenProvider
import com.sup.dev.java.tools.ToolsThreads
import java.lang.RuntimeException
import java.util.concurrent.TimeUnit


object ControllerGoogleAuth {

    private var serverClientId = ""

    var tokenPostExecutor: (String?, callback: (String?) -> Unit) -> Unit = { token, callback -> callback.invoke(token) }
    private var googleAccount: GoogleSignInAccount? = null
    private var onLoginFailed: () -> Unit = {}

    fun init(serverClientId: String, onLoginFailed: () -> Unit) {
        this.serverClientId = serverClientId
        this.onLoginFailed = onLoginFailed
    }

    fun getGooglePhotoUrl(): String? {
        if (googleAccount == null || googleAccount!!.photoUrl == null) return null
        return googleAccount!!.photoUrl!!.toString()
    }

    fun instanceTokenProvider(): TokenProvider {
        return object : TokenProvider {

            override fun getToken(callbackSource: (String?) -> Unit) {
                ControllerGoogleAuth.getToken {
                    tokenPostExecutor.invoke(it) { token ->
                        callbackSource.invoke(token)
                    }
                }
            }

            override fun clearToken() {
                ControllerGoogleAuth.clearToken()
            }

            override fun onLoginFailed() {
                ControllerGoogleAuth.onLoginFailed()
            }
        }
    }

    fun logout(callback: (() -> Unit)) {
        getClient { googleApiClient ->
            ToolsThreads.main {
                googleAccount = null
                Auth.GoogleSignInApi.signOut(googleApiClient)
                callback.invoke()
                googleApiClient.disconnect()
            }
        }
    }

    fun getToken(tryCount: Int = 5, onResult: (String?) -> Unit) {
        if (googleAccount != null) {
            onResult.invoke(googleAccount!!.idToken)
            return
        }
        getGoogleToken { googleAccount ->
            ControllerGoogleAuth.googleAccount = googleAccount
            if ((googleAccount?.idToken == null || googleAccount.idToken!!.isEmpty()) && tryCount > 0) {
                ToolsThreads.main(200) { getToken(tryCount - 1, onResult) }
            } else {
                if (ToolsAndroid.isDebug()) {
                    if (googleAccount?.idToken == null || googleAccount.idToken!!.isEmpty()) {
                        throw RuntimeException("GOOGLE DONT'T PROVIDE TOKEN [${googleAccount?.idToken}] [${googleAccount}]")
                    }
                }
                onResult.invoke(googleAccount?.idToken)
            }
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

    //
    //  Autch
    //

    fun getGoogleToken(onComplete: (GoogleSignInAccount?) -> Unit) {
        getClient { googleApiClient ->
            val googleSignInResult = Auth.GoogleSignInApi.silentSignIn(googleApiClient).await(500, TimeUnit.MILLISECONDS)

            if (googleSignInResult.isSuccess && googleSignInResult.signInAccount != null) {
                ToolsThreads.main { onComplete.invoke(googleSignInResult.signInAccount) }
                googleApiClient.disconnect()
            } else {

                ToolsIntent.startIntentForResult(Auth.GoogleSignInApi.getSignInIntent(googleApiClient)) { resultCode, intent ->
                    val result = Auth.GoogleSignInApi.getSignInResultFromIntent(intent)

                    if (result == null || result.signInAccount?.idToken == null) {
                        onComplete.invoke(null)
                    } else
                        onComplete.invoke(result.signInAccount)

                    googleApiClient.disconnect()
                }
            }
        }
    }

    //
    //  Support
    //


    private fun getClient(onConnect: (GoogleApiClient) -> Unit) {

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(serverClientId)
                .build()

        val client = GoogleApiClient.Builder(SupAndroid.activity!!)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .setGravityForPopups(Gravity.BOTTOM or Gravity.CENTER)
                .build()

        ToolsThreads.thread {
            client.blockingConnect()
            onConnect.invoke(client)
        }

    }


}