package com.sayzen.devsupandroidgoogle

import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.formats.NativeAdOptions
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.sup.dev.android.app.SupAndroid
import com.sup.dev.android.tools.ToolsAndroid
import com.sup.dev.java.libs.debug.info

object ControllerAdsNative {

    private var adLoader: AdLoader? = null
    private var key_ads = arrayOf(ControllerAds.DEBUG_AD)
    private var keyAd = 0

    fun init(){
        this.key_ads = if (ControllerAds.isDebug) arrayOf(ControllerAds.DEBUG_AD) else key_ads
        loadAd(true)
    }

    fun loadAd(force: Boolean = false){
        if (adLoader != null && adLoader!!.isLoading) return
        if (!force && !ToolsAndroid.appIsVisible()) return

        var key_ad = ""
        if (!ControllerAds.isDebug) {
            keyAd++
            if (keyAd >= key_ads.size) keyAd = 0
            key_ad = key_ads[keyAd]
        }

        adLoader = AdLoader.Builder(SupAndroid.appContext, key_ad)
                .forUnifiedNativeAd { ad : UnifiedNativeAd ->
                    info("XAD", "Ad is loaded " + ad)
                    // Show the ad.
                }
                .withAdListener(object : AdListener() {
                    override fun onAdFailedToLoad(errorCode: Int) {
                        info("XAD",  "onAdFailedToLoad " + errorCode)
                        // Handle the failure by logging, altering the UI, and so on.
                    }
                })
                .withNativeAdOptions(NativeAdOptions.Builder()
                        // Methods in the NativeAdOptions.Builder class can be
                        // used here to specify individual options settings.
                        .build())
                .build()
    }

}