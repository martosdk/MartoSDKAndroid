package com.ironsource.adapters.custom.marto;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import com.ironsource.mediationsdk.adunit.adapter.BaseAdapter;
import com.ironsource.mediationsdk.adunit.adapter.listener.NetworkInitializationListener;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdData;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdapterErrorType;
import com.mt.bidad.sdk.openapi.CTAdConfig;
import com.mt.bidad.sdk.openapi.CTAdError;
import com.mt.bidad.sdk.openapi.CTAdRequest;
import com.mt.bidad.sdk.openapi.CTAdSdk;
import com.mt.bidad.sdk.openapi.CTGlobalSettings;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class MartoCustomAdapter extends BaseAdapter {
    public static final String TAG = "CTCustomAdapter";

    @Override
    public void setConsent(boolean consent) {
        CTGlobalSettings.getInstance().setGdpr(consent);
    }

    @Override
    public void init(@NotNull AdData adData, @NotNull Context context,
                     @Nullable NetworkInitializationListener networkInitializationListener) {
        Log.i(TAG, "init");
        Map<String, Object> configuration = adData.getConfiguration();
        if (configuration == null || configuration.isEmpty()) {
            String msg = "configuration is null or empty";
            Log.i(TAG, "init failed " + msg);
            if (networkInitializationListener != null) {
                networkInitializationListener.onInitFailed(CTCustomConstants.DEFAULT_ERROR_CODE, msg);
            }
            return;
        }

        CTGlobalSettings.getInstance().setMediation(CTCustomConstants.MEDIATION);

        String appId = "";
        Object appIdObj = configuration.get(CTCustomConstants.KEY_APP_ID);
        if (appIdObj instanceof String) {
            appId = (String) appIdObj;
        }
        Log.i(MartoCustomAdapter.TAG, "appId:" + appId);
        CTAdConfig config = new CTAdConfig.Builder()
                .setAppId(appId)
                .build();
        CTAdSdk.init(context, config, new CTAdSdk.CTInitListener() {
            @Override
            public void onInitSuccess() {
                Log.i(TAG, "onInitSuccess");
                if (networkInitializationListener != null) {
                    networkInitializationListener.onInitSuccess();
                }
            }

            @Override
            public void onInitFailed(CTAdError adError) {
                int code = 0;
                String msg = "";
                if (adError != null) {
                    code = adError.getErrorCode();
                    msg = adError.getErrorMsg();
                }
                Log.i(TAG, "onInitFailed msg:" + msg);
                if (networkInitializationListener != null) {
                    networkInitializationListener.onInitFailed(code, msg);
                }
            }
        });
    }

    @Nullable
    @Override
    public String getNetworkSDKVersion() {
        return CTAdSdk.getSdkVersionName();
    }

    @NotNull
    @Override
    public String getAdapterVersion() {
        return BuildConfig.NETWORK_VERSION_NAME;
    }

    public static CTAdRequest.Builder getAdRequestBuilder(Map<String, Object> configuration) {
        if (configuration == null || configuration.isEmpty()) {
            return null;
        }

        String placementId = "";
        Object pidObj = configuration.get(CTCustomConstants.KEY_PLACEMENT_ID);
        Log.i(MartoCustomAdapter.TAG, "pid:" + pidObj);
        if (pidObj instanceof String) {
            placementId = (String) pidObj;
        }
        return new CTAdRequest.Builder()
                .setPlacementId(placementId);
    }

    public static Pair<AdapterErrorType, Pair<Integer, String>> getAdErrorTypePair(CTAdError error) {
        AdapterErrorType errorType = AdapterErrorType.ADAPTER_ERROR_TYPE_INTERNAL;
        int code = CTCustomConstants.DEFAULT_ERROR_CODE;
        String msg = "";
        if (error != null) {
            code = error.getErrorCode();
            msg = error.getErrorMsg();
            if (code == CTAdError.CODE_NO_AD) {
                errorType = AdapterErrorType.ADAPTER_ERROR_TYPE_NO_FILL;
            } else if (code == CTAdError.CODE_AD_EXPIRED) {
                errorType = AdapterErrorType.ADAPTER_ERROR_TYPE_AD_EXPIRED;
            }
        }

        return new Pair<>(errorType, new Pair<>(code, msg));
    }

    public static Pair<Integer, String> getAdErrorPair(CTAdError error) {
        int code = CTCustomConstants.DEFAULT_ERROR_CODE;
        String msg = "";
        if (error != null) {
            code = error.getErrorCode();
            msg = error.getErrorMsg();
        }

        return new Pair<>(code, msg);
    }
}
