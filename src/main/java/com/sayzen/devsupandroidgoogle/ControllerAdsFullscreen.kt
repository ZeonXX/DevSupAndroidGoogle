package com.sayzen.devsupandroidgoogle

import android.annotation.SuppressLint
import android.os.Bundle
import com.google.ads.consent.ConsentInformation
import com.google.ads.consent.ConsentStatus
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.sup.dev.android.app.SupAndroid
import com.sup.dev.android.tools.ToolsAndroid
import com.sup.dev.java.libs.debug.err
import com.sup.dev.java.tools.ToolsThreads

object ControllerAdsFullscreen {

    private var ad: InterstitialAd? = null
    private var lastShow = System.currentTimeMillis()
    private var key_ads = arrayOf(ControllerAds.DEBUG_AD)
    private var keyAd = 0

    fun init(key_ads: Array<String>) {
        this.key_ads = if (ControllerAds.isDebug) arrayOf(ControllerAds.DEBUG_AD) else key_ads
        loadAd(true)
    }

    @SuppressLint("MissingPermission")
    private fun loadAd(force: Boolean = false) {
        if (ad != null && ad!!.isLoading) return
        if (!force && !ToolsAndroid.appIsVisible()) return

        var key_ad = ""
        if (!ControllerAds.isDebug) {
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
        if (ConsentInformation.getInstance(SupAndroid.appContext).isRequestLocationInEeaOrUnknown && ControllerAdsConsent.status != ConsentStatus.PERSONALIZED) {
            extras.putString("npa", "1")
        }
        MobileAds.initialize(SupAndroid.appContext, ControllerAds.key_app)


        ad!!.loadAd(AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter::class.java, extras).build())
    }


    fun showIfNeed() {
        ControllerAdsConsent.updateConsent()
        if (System.currentTimeMillis() <= lastShow + 1000 * 60 * 3) return

        if (ad!!.isLoaded && SupAndroid.activityIsVisible) {
            ad!!.show()
            lastShow = System.currentTimeMillis()
        } else {
            if (!ad!!.isLoading) loadAd()
        }
    }



}