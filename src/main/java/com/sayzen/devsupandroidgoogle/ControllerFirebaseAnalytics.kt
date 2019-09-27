package com.sayzen.devsupandroidgoogle

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.sup.dev.android.app.SupAndroid

object ControllerFirebaseAnalytics {

    private val firebaseAnalytics = FirebaseAnalytics.getInstance(SupAndroid.appContext!!)

    fun post_1(){
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "id_plus_clicked")
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "name_plus_clicked")
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "category_plus_clicked")
        bundle.putString(FirebaseAnalytics.Param.ITEM_BRAND, "brand_plus_clicked")
        bundle.putString(FirebaseAnalytics.Param.ITEM_LOCATION_ID, "location_id_plus_clicked")
        bundle.putString(FirebaseAnalytics.Param.ITEM_VARIANT, "variant_id_plus_clicked")
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
    }

    fun post_2(){
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "id_search_clicked")
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "name_search_clicked")
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "category_search_clicked")
        bundle.putString(FirebaseAnalytics.Param.ITEM_BRAND, "brand_search_clicked")
        bundle.putString(FirebaseAnalytics.Param.ITEM_LOCATION_ID, "location_id_search_clicked")
        bundle.putString(FirebaseAnalytics.Param.ITEM_VARIANT, "variant_search_clicked")
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
    }


}