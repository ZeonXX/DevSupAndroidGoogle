package com.sayzen.devsupandroidgoogle

import android.annotation.SuppressLint
import android.os.Bundle
import com.google.ads.consent.*
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.sup.dev.android.app.SupAndroid
import com.sup.dev.android.tools.ToolsAndroid
import com.sup.dev.android.tools.ToolsResources
import com.sup.dev.java.classes.items.ItemNullable
import com.sup.dev.java.libs.debug.err
import com.sup.dev.java.libs.debug.log
import com.sup.dev.java.tools.ToolsThreads
import java.net.URL

object ControllerAds {

    private val DEBUG_PROJECT = "ca-app-pub-3940256099942544~3347511713"
    private val DEBUG_APP = "ca-app-pub-3940256099942544/1033173712"

    private var ad: InterstitialAd? = null
    private var lastShow = System.currentTimeMillis()
    private var isDebug = false
    private var key_pub = ""
    private var key_ads = arrayOf(DEBUG_APP)
    private var key_project = DEBUG_PROJECT
    private var status = ConsentStatus.UNKNOWN
    private var keyAd = 0


    fun init(isDebug: Boolean, key_pub:String, key_project: String, key_ads: Array<String>) {
        this.isDebug = isDebug
        this.key_pub = key_pub
        this.key_project = if (isDebug) DEBUG_PROJECT else key_project
        this.key_ads = if (isDebug) arrayOf(DEBUG_APP) else key_ads
        loadAd(true)
    }

    fun showIfNeed() {
        if (System.currentTimeMillis() <= lastShow + 1000 * 60 * 3) return

        if (ad!!.isLoaded && SupAndroid.activityIsVisible) {
            ad!!.show()
            lastShow = System.currentTimeMillis()
        } else {
            if (!ad!!.isLoading) loadAd()
        }
    }

    @SuppressLint("MissingPermission")
    private fun loadAd(force: Boolean = false) {
        if (ad != null && ad!!.isLoading) return
        if (!force && !ToolsAndroid.appIsVisible()) return

        var key_ad = ""
        if (!isDebug) {
            keyAd++
            if (keyAd >= key_ads.size) keyAd = 0
            key_ad = key_ads[keyAd]
        }

        ad = InterstitialAd(SupAndroid.appContext!!)
        ad!!.adUnitId = key_ad
        ad!!.adListener = object : AdListener() {

            override fun onAdLoaded() {
                err("XX onAdLoaded")
            }

            override fun onAdFailedToLoad(i: Int) {
                err("XX onAdFailedToLoad $i")
                ToolsThreads.main(1000 * 10) { loadAd() }
            }

            override fun onAdClosed() {
                loadAd()
            }

        }


        val extras = Bundle()
        extras.putString("max_ad_content_rating", "T")
        if (ConsentInformation.getInstance(SupAndroid.appContext).isRequestLocationInEeaOrUnknown && status != ConsentStatus.PERSONALIZED) {
            extras.putString("npa", "1")
        }
        MobileAds.initialize(SupAndroid.appContext, key_project)


        ad!!.loadAd(AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter::class.java, extras).build())
    }

    //
    //  Consent
    //


    fun updateConsent() {
        val consentInformation = ConsentInformation.getInstance(SupAndroid.activity)
        // consentInformation.addTestDevice("C4526D38EE33CA71F16EE8B8096FA4C6")
        // consentInformation.debugGeography = DebugGeography.DEBUG_GEOGRAPHY_EEA
        val publisherIds = arrayOf(key_pub)
        consentInformation.requestConsentInfoUpdate(publisherIds, object : ConsentInfoUpdateListener {
            override fun onConsentInfoUpdated(consentStatus: ConsentStatus) {

                ControllerAds.status = status
                if (consentStatus == ConsentStatus.UNKNOWN) showConsentForm()
            }

            override fun onFailedToUpdateConsentInfo(errorDescription: String) {

            }
        })
    }

    private fun showConsentForm() {
        if (SupAndroid.activity == null) {
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
        build.load()
    }


}