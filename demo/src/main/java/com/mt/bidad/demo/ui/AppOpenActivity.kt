package com.mt.bidad.demo.ui

import android.os.Bundle
import com.mt.bidad.demo.databinding.ActivityAppOpenBinding
import com.mt.bidad.sdk.openapi.CTAdError
import com.mt.bidad.sdk.openapi.CTAdRequest
import com.mt.bidad.sdk.openapi.CTBaseAd
import com.mt.bidad.sdk.openapi.splash.CTSplash
import com.mt.bidad.sdk.openapi.splash.CTSplashAdListener

class AppOpenActivity : BaseActivity(), CTSplashAdListener {
    private lateinit var binding: ActivityAppOpenBinding
    private var splash: CTSplash? = null
    private val placementId = SPLASH_PLACEMENT_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppOpenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loadBtn.setOnClickListener {
            loadAd()
        }
    }

    fun loadAd() {
        val adRequest = CTAdRequest.Builder()
            .setPlacementId(placementId)
            .setMute(true)
            .build()
        splash = CTSplash(adRequest).apply {
            setSplashAdListener(this@AppOpenActivity)
            binding.statusTv.text = "appOpen loading"
            loadAd()
        }
    }

    override fun onLoaded(baseAd: CTBaseAd) {
        binding.statusTv.append("\nappOpen loaded")
        splash?.showAd(this)
    }

    override fun onLoadFailed(adError: CTAdError?) {
        binding.statusTv.append("\nappOpen load failed code ${adError?.errorCode} msg ${adError?.errorMsg}")
    }

    override fun onClosed(baseAd: CTBaseAd) {
        binding.statusTv.append("\nappOpen closed")
        splash = null
    }

    override fun onShown(baseAd: CTBaseAd) {
        binding.statusTv.append("\nappOpen shown")
    }

    override fun onShowFailed(baseAd: CTBaseAd, adError: CTAdError) {
        binding.statusTv.append("\nappOpen show failed code ${adError.errorCode} msg ${adError.errorMsg}")
    }

    override fun onClicked(baseAd: CTBaseAd) {
        binding.statusTv.append("\nappOpen clicked")
    }
}