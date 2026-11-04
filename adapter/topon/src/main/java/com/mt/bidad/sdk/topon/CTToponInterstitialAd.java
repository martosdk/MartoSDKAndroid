package com.mt.bidad.sdk.topon;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.util.Pair;

import com.secmtp.sdk.core.api.ATBiddingListener;
import com.secmtp.sdk.core.api.ATInitMediation;
import com.secmtp.sdk.interstitial.unitgroup.api.CustomInterstitialAdapter;

import java.util.Map;

import com.mt.bidad.sdk.openapi.CTAdError;
import com.mt.bidad.sdk.openapi.CTAdRequest;
import com.mt.bidad.sdk.openapi.CTAdSdk;
import com.mt.bidad.sdk.openapi.CTBaseAd;
import com.mt.bidad.sdk.openapi.CTGlobalSettings;
import com.mt.bidad.sdk.openapi.interstitial.CTInterstitial;
import com.mt.bidad.sdk.openapi.interstitial.CTInterstitialAdListener;

public class CTToponInterstitialAd extends CustomInterstitialAdapter implements CTInterstitialAdListener {

    private String mUnitId;
    private CTInterstitial mCTInterstitial;
    private boolean mC2SBidding;
    private ATBiddingListener mBiddingListener;

    @Override
    public boolean startBiddingRequest(Context context, Map<String, Object> serverExtra, Map<String, Object> localExtra, ATBiddingListener biddingListener) {
        Log.i(CTToponMediation.TAG, "interstitial startBiddingRequest");
        mC2SBidding = true;
        this.mBiddingListener = biddingListener;
        loadCustomNetworkAd(context, serverExtra, localExtra);
        return true;
    }

    @Override
    public boolean setUserDataConsent(Context context, boolean isConsent, boolean isEUTraffic) {
        CTGlobalSettings.getInstance().setGdpr(isConsent);
        return true;
    }

    @Override
    public void loadCustomNetworkAd(Context context, Map<String, Object> serviceExtras, Map<String, Object> localExtras) {
        mUnitId = ATInitMediation.getStringFromMap(serviceExtras, CTToponMediation.KEY_UNIT_ID);
        Log.i(CTToponMediation.TAG, "loadInterstitial mUnitId:" + mUnitId);
        CTToponMediation.init(context, serviceExtras, new CTAdSdk.CTInitListener() {
            @Override
            public void onInitSuccess() {
                loadAd();
            }

            @Override
            public void onInitFailed(CTAdError adError) {
                onAdLoadFailed(adError);
            }
        });
    }

    private void loadAd() {
        CTAdRequest adRequest = new CTAdRequest.Builder()
                .setPlacementId(getNetworkPlacementId())
                .build();
        mCTInterstitial = new CTInterstitial(adRequest);
        mCTInterstitial.setInterstitialAdListener(this);
        mCTInterstitial.loadAd();
    }

    @Override
    public void show(Activity activity) {
        Log.i(CTToponMediation.TAG, "interstitial showAd");
        if (mCTInterstitial != null) {
            mCTInterstitial.showAd(activity);
        }
    }

    @Override
    public void destory() {
        mLoadListener = null;
        mImpressListener = null;
    }

    @Override
    public boolean isAdReady() {
        if (mCTInterstitial != null) {
            return mCTInterstitial.isReady();
        }
        return false;
    }

    @Override
    public String getNetworkPlacementId() {
        return mUnitId;
    }

    @Override
    public String getNetworkSDKVersion() {
        return CTToponMediation.getNetworkSDKVersion();
    }

    @Override
    public String getNetworkName() {
        return CTToponMediation.getNetworkName();
    }

    @Override
    public void onLoaded(CTBaseAd baseAd) {
        if (mC2SBidding) {
            CTToponMediation.onC2SBiddingSuccess(mCTInterstitial, baseAd, mBiddingListener, null);
        } else {
            if (mLoadListener != null) {
                mLoadListener.onAdCacheLoaded();
            }
        }
    }

    @Override
    public void onLoadFailed(CTAdError adError) {
        onAdLoadFailed(adError);
    }

    private void onAdLoadFailed(CTAdError adError) {
        Pair<String, String> errorPair = CTToponMediation.getAdError(adError);
        if (mC2SBidding) {
            CTToponMediation.onC2SBiddingFailed(adError, mBiddingListener);
        } else {
            if (mLoadListener != null) {
                mLoadListener.onAdLoadError(errorPair.first, errorPair.second);
            }
        }
    }

    @Override
    public void onClosed(CTBaseAd baseAd) {
        if (mImpressListener != null) {
            mImpressListener.onInterstitialAdClose();
        }
    }

    @Override
    public void onShown(CTBaseAd baseAd) {
        if (mImpressListener != null) {
            mImpressListener.onInterstitialAdShow();
        }
    }

    @Override
    public void onClicked(CTBaseAd baseAd) {
        if (mImpressListener != null) {
            mImpressListener.onInterstitialAdClicked();
        }
    }

    @Override
    public void onVideoPlay(CTBaseAd baseAd) {
        if (mImpressListener != null) {
            mImpressListener.onInterstitialAdVideoStart();
        }
    }

    @Override
    public void onVideoComplete(CTBaseAd baseAd) {
        if (mImpressListener != null) {
            mImpressListener.onInterstitialAdVideoEnd();
        }
    }
}
