package com.sayzen.devsupandroidgoogle

import android.view.Gravity
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.sup.dev.android.app.SupAndroid
import com.sup.dev.android.tools.ToolsIntent
import com.sup.dev.java.tools.ToolsThreads
import java.util.concurrent.TimeUnit

object GoogleAuth {

    fun getGoogleToken(onComplete: (GoogleSignInAccount?) -> Unit) {
        getClient { googleApiClient ->
            val googleSignInResult = Auth.GoogleSignInApi.silentSignIn(googleApiClient).await(500, TimeUnit.MILLISECONDS)

            if (googleSignInResult.isSuccess && googleSignInResult.signInAccount != null){
                ToolsThreads.main { onComplete.invoke(googleSignInResult.signInAccount) }
                googleApiClient.disconnect()
            }else {

                ToolsIntent.startIntentForResult(Auth.GoogleSignInApi.getSignInIntent(googleApiClient)) { resultCode, intent ->
                    val result = Auth.GoogleSignInApi.getSignInResultFromIntent(intent)
                    if (resultCode == 0 || result == null || !result.isSuccess) {
                        onComplete.invoke(null)
                    } else
                        onComplete.invoke(result.signInAccount)

                    googleApiClient.disconnect()
                }
            }
        }
    }

    //
    //  Exit
    //

    fun exit(onExit: () -> Unit) {
        getClient { googleApiClient ->
            ToolsThreads.main {
                Auth.GoogleSignInApi.signOut(googleApiClient)
                onExit.invoke()
                googleApiClient.disconnect()
            }
        }
    }

    //
    //  Support
    //


    private fun getClient(onConnect: (GoogleApiClient) -> Unit) {

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("276237287601-6e9aoah4uivbjh6lnn1l9hna6taljd9u.apps.googleusercontent.com")
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