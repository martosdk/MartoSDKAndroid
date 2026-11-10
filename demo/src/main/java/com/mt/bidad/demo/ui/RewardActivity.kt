package com.mt.bidad.demo.ui

import android.os.Bundle
import com.mt.bidad.demo.databinding.ActivityRewardBinding
import com.mt.bidad.sdk.openapi.CTAdError
import com.mt.bidad.sdk.openapi.CTAdRequest
import com.mt.bidad.sdk.openapi.CTBaseAd
import com.mt.bidad.sdk.openapi.reward.CTReward
import com.mt.bidad.sdk.openapi.reward.CTRewardAdListener

class RewardActivity : BaseActivity(), CTRewardAdListener {

    private lateinit var binding: ActivityRewardBinding
    private var ctReward: CTReward? = null
    private val placementId = REWARD_PLACEMENT_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRewardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loadBtn.setOnClickListener {
            loadAd()
        }
        binding.readyBtn.setOnClickListener {
            isReady()
        }
        binding.showBtn.setOnClickListener {
            if (ctReward == null) {
                showToast("please loadAd")
                return@setOnClickListener
            }
            if (isReady()) {
                showAd()
            }
        }
    }

    private fun loadAd() {
        val adRequest = CTAdRequest.Builder()
            .setPlacementId(placementId)
            .setMute(true)
            .build()
        ctReward = CTReward(adRequest)
        ctReward?.apply {
            setRewardAdListener(this@RewardActivity)
            binding.statusTv.text = "reward loading"
            loadAd()
        }
    }

    private fun isReady(): Boolean {
        val ready = ctReward?.isReady ?: false
        showToast("interstitial isReady $ready")
        return ready
    }

    private fun showAd() {
        ctReward?.showAd(this)
    }

    override fun onLoaded(baseAd: CTBaseAd?) {
        binding.statusTv.append("\nreward loaded")
    }

    override fun onLoadFailed(adError: CTAdError?) {
        binding.statusTv.append("\nreward load failed code ${adError?.errorCode} msg ${adError?.errorMsg}")
    }

    override fun onShown(baseAd: CTBaseAd?) {
        binding.statusTv.append("\nreward onShown")
    }

    override fun onShowFailed(
        baseAd: CTBaseAd?,
        adError: CTAdError?
    ) {
        binding.statusTv.append("\nreward onShowFailed code ${adError?.errorCode} msg ${adError?.errorMsg}")
    }

    override fun onClicked(baseAd: CTBaseAd?) {
        binding.statusTv.append("\nreward onClicked")
    }

    override fun onClosed(baseAd: CTBaseAd?) {
        binding.statusTv.append("\nreward onClosed")
        ctReward = null
    }

    override fun onRewarded(baseAd: CTBaseAd?, rewardMap: Map<String?, Any?>?) {
        binding.statusTv.append("\nreward onRewarded $rewardMap")
    }
}