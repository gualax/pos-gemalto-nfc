package com.example.mygemaltonfc.sdk;

import android.content.Context;
import android.util.Log;

import com.example.mygemaltonfc.utils.Constants;
import com.gemalto.mfs.mwsdk.mobilegateway.MGConnectionConfiguration;
import com.gemalto.mfs.mwsdk.mobilegateway.MGSDKConfigurationState;
import com.gemalto.mfs.mwsdk.mobilegateway.MGTransactionHistoryConfiguration;
import com.gemalto.mfs.mwsdk.mobilegateway.MGWalletConfiguration;
import com.gemalto.mfs.mwsdk.mobilegateway.MobileGatewayManager;
import com.gemalto.mfs.mwsdk.mobilegateway.exception.MGConfigurationException;
import com.gemalto.mfs.mwsdk.mobilegateway.exception.MGStorageConfigurationException;

import static com.gemalto.mfs.mwsdk.mobilegateway.MGSDKConfigurationState.NOT_CONFIGURED;

public class SDKHelper {

    public static void initMGSDK(Context context) throws MGConfigurationException {
        MGSDKConfigurationState configurationState = MobileGatewayManager.INSTANCE.getConfigurationState();
        if (configurationState != NOT_CONFIGURED) {
            return;
        }

        //Configure MG configuration
        MGConnectionConfiguration connectionConfiguration = new MGConnectionConfiguration
                .Builder()
                .setConnectionParameters(Constants.MG_CONNECTION_URL_VTS_LAB,
                        Constants.MG_CONNECTION_TIMEOUT,
                        Constants.MG_CONNECTION_READ_TIMEOUT)
                .setRetryParameters(Constants.MG_CONNECTION_RETRY_COUNT,
                        Constants.MG_CONNECTION_RETRY_INTERVAL)
                .build();

        //Configure wallet configuration
        MGWalletConfiguration walletConfiguration = new MGWalletConfiguration
                .Builder()
                .setWalletParameters(Constants.WALLET_PROVIDER_ID_VTS_LAB)
                .build();

        //Configure Transaction History
        MGTransactionHistoryConfiguration transactionConfiguration = new MGTransactionHistoryConfiguration
                .Builder()
                .setConnectionParameters(
                        Constants.MG_TRANSACTION_HISTORY_CONNECTION_URL_VTS_LAB)
                .build();
        try {
            if (configurationState == NOT_CONFIGURED) {
                MobileGatewayManager.INSTANCE.configure(context, connectionConfiguration
                        , walletConfiguration, transactionConfiguration);
            }
        } catch (MGStorageConfigurationException e) {
            Log.e("MG Config", "", e);
        } catch (MGConfigurationException exception) {
            Log.e("MG Config", "MG MGConfigurationException " + exception.getLocalizedMessage(), exception);
        }

        Log.d("MG Config", "MG Configuration initialised");
    }
}
