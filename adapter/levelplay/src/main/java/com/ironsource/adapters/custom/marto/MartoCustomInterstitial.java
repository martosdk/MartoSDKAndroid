package com.ironsource.adapters.custom.marto;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.util.Pair;

import com.ironsource.mediationsdk.adunit.adapter.BaseInterstitial;
import com.ironsource.mediationsdk.adunit.adapter.listener.InterstitialAdListener;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdData;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdapterErrorType;
import com.ironsource.mediationsdk.model.NetworkSettings;
import com.mt.bidad.sdk.openapi.CTAdError;
import com.mt.bidad.sdk.openapi.CTBaseAd;
import com.mt.bidad.sdk.openapi.interstitial.CTInterstitial;
import com.mt.bidad.sdk.openapi.interstitial.CTInterstitialAdListener;

import org.jetbrains.annotations.NotNull;

public class MartoCustomInterstitial extends BaseInterstitial<MartoCustomAdapter> implements CTInterstitialAdListener {

    private CTInterstitial mCTInterstitial;
    private InterstitialAdListener mListener;

    public MartoCustomInterstitial(NetworkSettings networkSettings) {
        super(networkSettings);
    }

    @Override
    public void loadAd(@NotNull AdData adData, @NotNull Context context, @NotNull InterstitialAdListener listener) {
        Log.i(MartoCustomAdapter.TAG, "load interstitial");
        this.mListener = listener;
        mCTInterstitial = new CTInterstitial(MartoCustomAdapter.getAdRequestBuilder(
                adData.getConfiguration()).build());
        mCTInterstitial.setInterstitialAdListener(this);
        mCTInterstitial.loadAd();
    }

    @Override
    public void showAd(@NotNull AdData adData, @NotNull Activity activity, @NotNull InterstitialAdListener listener) {
        Log.i(MartoCustomAdapter.TAG, "interstitial showAd");
        if (mCTInterstitial != null) {
            mCTInterstitial.showAd(activity);
        }
    }

    @Override
    public boolean isAdAvailable(@NotNull AdData adData) {
        if (mCTInterstitial != null) {
            return mCTInterstitial.isReady();
        }
        return false;
    }

    @Override
    public void destroyAd(@NotNull AdData adData) {
        // do Nothing
    }

    @Override
    public void onLoaded(CTBaseAd baseAd) {
        if (mListener != null) {
            mListener.onAdLoadSuccess();
        }
    }

    @Override
    public void onLoadFailed(CTAdError adError) {
        if (mListener != null) {
            Pair<AdapterErrorType, Pair<Integer, String>> adErrorTypePair = MartoCustomAdapter.getAdErrorTypePair(adError);
            mListener.onAdLoadFailed(adErrorTypePair.first, adErrorTypePair.second.first, adErrorTypePair.second.second);
        }
    }

    @Override
    public void onShown(CTBaseAd baseAd) {
        if (mListener != null) {
            mListener.onAdOpened();
        }
    }

    @Override
    public void onShowFailed(CTBaseAd baseAd, CTAdError adError) {
        if (mListener != null) {
            Pair<Integer, String> errorPair = MartoCustomAdapter.getAdErrorPair(adError);
            mListener.onAdShowFailed(errorPair.first, errorPair.second);
        }
    }

    @Override
    public void onClicked(CTBaseAd baseAd) {
        if (mListener != null) {
            mListener.onAdClicked();
        }
    }

    @Override
    public void onClosed(CTBaseAd baseAd) {
        if (mListener != null) {
            mListener.onAdClosed();
        }
    }
}
