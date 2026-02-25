package com.autotranslate.app.ads

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

object AdManager {
    const val BANNER_AD_ID = "ca-app-pub-2039704869658818/3708649712"
    const val INTERSTITIAL_AD_ID = "ca-app-pub-2039704869658818/4757319149"

    private var interstitialAd: InterstitialAd? = null
    private var translationsSinceLastAd = 0
    private const val AD_FREQUENCY = 5

    fun loadInterstitial(context: Context) {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(context, INTERSTITIAL_AD_ID, adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                }
            })
    }

    fun showInterstitialIfReady(activity: Activity): Boolean {
        translationsSinceLastAd++
        if (translationsSinceLastAd >= AD_FREQUENCY) {
            translationsSinceLastAd = 0
            interstitialAd?.let { ad ->
                ad.show(activity)
                interstitialAd = null
                loadInterstitial(activity)
                return true
            }
        }
        return false
    }
}
