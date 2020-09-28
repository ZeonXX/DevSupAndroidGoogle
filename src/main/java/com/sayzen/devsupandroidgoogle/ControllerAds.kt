package com.sayzen.devsupandroidgoogle

import com.google.ads.consent.*
import com.sup.dev.android.app.SupAndroid
import com.sup.dev.android.tools.ToolsStorage
import com.sup.dev.java.classes.items.ItemNullable
import com.sup.dev.java.libs.debug.err
import com.sup.dev.java.tools.ToolsThreads
import java.lang.Exception
import java.net.URL

object ControllerAds {

    var isDebug = BuildConfig.DEBUG
    var key_pub = ""
    var key_app = ""


    fun init(key_pub:String, key_project: String) {
        this.key_pub = key_pub
        this.key_app = if (isDebug) "" else key_project
    }

    //
    //   Consent
    //

    var status = ConsentStatus.UNKNOWN

    fun updateConsent() {
        val consentInformation = ConsentInformation.getInstance(SupAndroid.activity)
        // consentInformation.addTestDevice("C4526D38EE33CA71F16EE8B8096FA4C6")
        // consentInformation.debugGeography = DebugGeography.DEBUG_GEOGRAPHY_EEA
        val publisherIds = arrayOf(key_pub)
        consentInformation.requestConsentInfoUpdate(publisherIds, object : ConsentInfoUpdateListener {
            override fun onConsentInfoUpdated(consentStatus: ConsentStatus) {

                if (consentStatus == ConsentStatus.UNKNOWN) showConsentForm()
            }

            override fun onFailedToUpdateConsentInfo(errorDescription: String) {

            }
        })
    }

    private fun showConsentForm() {
        if(ToolsStorage.getBoolean("showConsentForm", false))return
        try {
            if (SupAndroid.activity == null && !SupAndroid.activityIsDestroy) {
                ToolsThreads.main(5000) { showConsentForm() }
                return
            }

            val privacyUrl = URL("http://sayzen.ru/eng.html")

            val formKeeper = ItemNullable<ConsentForm>(null)
            val build = ConsentForm.Builder(SupAndroid.activity, privacyUrl)
                    .withListener(object : ConsentFormListener() {
                        override fun onConsentFormLoaded() {
                            if (SupAndroid.activityIsVisible) formKeeper.a!!.show()
                            else ToolsThreads.main(5000) { showConsentForm() }
                        }

                        override fun onConsentFormOpened() {

                        }

                        override fun onConsentFormClosed(consentStatus: ConsentStatus?, userPrefersAdFree: Boolean?) {

                        }

                        override fun onConsentFormError(errorDescription: String?) {

                        }
                    })
                    .withPersonalizedAdsOption()
                    .withNonPersonalizedAdsOption()
                    .build()
            formKeeper.a = build
            ToolsStorage.put("showConsentForm", true)
            build.load()
        }catch (e: Exception){
            err(e)
        }
    }

}