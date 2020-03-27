package com.example.mygemaltonfc.sdk;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.mygemaltonfc.MainActivity;
import com.example.mygemaltonfc.R;
import com.gemalto.mfs.mwsdk.dcm.sdkconfig.DigitalizedCardManagerModule;
import com.gemalto.mfs.mwsdk.payment.cdcvm.DeviceCVMPreEntryReceiver;
import com.gemalto.mfs.mwsdk.payment.sdkconfig.PaymentServiceModule;
import com.gemalto.mfs.mwsdk.provisioning.sdkconfig.ProvisioningServiceModule;
import com.gemalto.mfs.mwsdk.sdkconfig.AbstractSDKConfigurator;
import com.gemalto.mfs.mwsdk.sdkconfig.SDKController;
import com.gemalto.mfs.mwsdk.sdkconfig.SDKControllerListener;
import com.gemalto.mfs.mwsdk.sdkconfig.SDKServiceState;
import com.gemalto.mfs.mwsdk.sdkconfig.SDKSetupProgressState;

public class SDKService extends Service {

    public static final String ACTION_INIT_DONE = "com.gemalto.sdkinitDone";

    private int FOREGROUND_NOTIFICATION_ID = 7;
    private static final boolean IS_BENCHMARK_LOG_NEEDED=true;
    private static final String BENCHMARK_TAG="INIT_CPS_SDK";
    private static final String STARTED=" started";
    private static final String ENDED=" ended";

    DigitalizedCardManagerModule digitalizedCardManagerModule;
    ProvisioningServiceModule provisioningServiceModule;
    PaymentServiceModule paymentServiceModule;

    private DeviceCVMPreEntryReceiver mPreEntryReceiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(FOREGROUND_NOTIFICATION_ID, buildNotification());
        }
        initializeCPSSDK();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPreEntryReceiver != null) {
            unregisterReceiver(mPreEntryReceiver);
        }
        mPreEntryReceiver = null;
    }

    private void initializeCPSSDK() {

        SDKController sdkController = SDKController.getInstance();
        final SDKServiceState state = sdkController.getSDKServiceState();
        switch (state) {
            case STATE_NOT_INITIALIZED:
                if(IS_BENCHMARK_LOG_NEEDED){
                    Log.i(BENCHMARK_TAG,BENCHMARK_TAG+":initialize"+STARTED);
                }
                if(IS_BENCHMARK_LOG_NEEDED){
                    Log.i(BENCHMARK_TAG,BENCHMARK_TAG+":initialize:sdkControllerDelegate Init"+STARTED);
                }
                sdkController.initialize(this,
                        new AbstractSDKConfigurator() {

                            @Override
                            public void initialize(SDKControllerListener sdkControllerListener) {

                                if(IS_BENCHMARK_LOG_NEEDED){
                                    Log.i(BENCHMARK_TAG,BENCHMARK_TAG+":initialize:sdkControllerDelegate Init"+ENDED);
                                }

                                if(IS_BENCHMARK_LOG_NEEDED){
                                    Log.i(BENCHMARK_TAG,BENCHMARK_TAG+":initialize:ModulesInit"+STARTED);
                                }

                                if(IS_BENCHMARK_LOG_NEEDED){
                                    Log.i(BENCHMARK_TAG,BENCHMARK_TAG+":initialize:ModulesInit:DCM"+STARTED);
                                }
                                // Mandatory: DCM (Digitalized Card Manager) module is in charge of
                                // managing the card data.
                                // The Provisioning Service and Payment Service need to use this.
                                // IMPORTANT: This has to be the first module to register.
                                if (digitalizedCardManagerModule == null) {
                                    digitalizedCardManagerModule = new DigitalizedCardManagerModule();
                                    register(digitalizedCardManagerModule, sdkControllerListener);
                                }
                                if(IS_BENCHMARK_LOG_NEEDED){
                                    Log.i(BENCHMARK_TAG,BENCHMARK_TAG+":initialize:ModulesInit:DCM"+ENDED);
                                }

                                if(IS_BENCHMARK_LOG_NEEDED){
                                    Log.i(BENCHMARK_TAG,BENCHMARK_TAG+":initialize:ModulesInit:Provisioning"+STARTED);
                                }

                                // Optional: If the application is using pull-mode, this module is
                                // necessary to be registered.
                                // In push-mode, the SDK will try to register the module in the background.
                                if (provisioningServiceModule == null) {
                                    provisioningServiceModule = new ProvisioningServiceModule();
                                    register(provisioningServiceModule, sdkControllerListener);
                                }

                                if(IS_BENCHMARK_LOG_NEEDED){
                                    Log.i(BENCHMARK_TAG,BENCHMARK_TAG+":initialize:ModulesInit:Provisioning"+ENDED);
                                }
                                if(IS_BENCHMARK_LOG_NEEDED){
                                    Log.i(BENCHMARK_TAG,BENCHMARK_TAG+":initialize:ModulesInit:Payment"+STARTED);
                                }

                                // Optional: The AbstractHCEService will try to initialize this module
                                // anyway.
                                // But if it doesn't cause remarkable delay in the launch of the app,
                                // it's recommended to be registered early to speed up the transaction
                                // process.
                                if (paymentServiceModule == null) {
                                    paymentServiceModule = new PaymentServiceModule();
                                    register(paymentServiceModule, sdkControllerListener);
                                }
                                if(IS_BENCHMARK_LOG_NEEDED){
                                    Log.i(BENCHMARK_TAG,BENCHMARK_TAG+":initialize:ModulesInit:Payment"+ENDED);
                                }
                                if(IS_BENCHMARK_LOG_NEEDED){
                                    Log.i(BENCHMARK_TAG,BENCHMARK_TAG+":initialize:ModulesInit"+ENDED);
                                }

                            }
                        },
                        new SDKControllerListener() {
                            @Override
                            public void onSetupProgress(SDKSetupProgressState sdkSetupProgressState, String s) {
                                Log.d("Service", "onSetupProgress completed");
                            }

                            @Override
                            public void onSetupComplete() {
                                Log.d("Service", "Initialization completed");
                                if(IS_BENCHMARK_LOG_NEEDED){
                                    Log.i(BENCHMARK_TAG,BENCHMARK_TAG+":initialize"+ENDED);
                                }
                                Toast.makeText(getApplicationContext(), "Initialization completed", Toast.LENGTH_SHORT).show();
                                broadcastInitComplete();
                                registerPreFpEntry();
                            }
                        }
                );
            case STATE_INITIALIZING_IN_PROGRESS:
                Log.d("Service", "STATE_INITIALIZING_IN_PROGRESS ");
                //it may be worth to watch out for this state.
                break;
            case STATE_INITIALIZED:
                Log.d("Service", "STATE_INITIALIZED ");
                broadcastInitComplete();
                break;
        }
    }

    private void registerPreFpEntry() {
        if (mPreEntryReceiver != null) {
            unregisterReceiver(mPreEntryReceiver);
            mPreEntryReceiver = null;
        }
        IntentFilter filter = new IntentFilter(Intent.ACTION_USER_PRESENT);
        mPreEntryReceiver = new DeviceCVMPreEntryReceiver();
        mPreEntryReceiver.init();
        registerReceiver(mPreEntryReceiver, filter);
    }



    /**********************************************************/
    /*                Private helpers                         */
    /**********************************************************/
    private void broadcastInitComplete() {
        Intent sdkInitDone = new Intent(ACTION_INIT_DONE);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(sdkInitDone);
    }

    private Notification buildNotification() {
        Log.d("Service", "Notification is initialized in foreground");
        String CHANNEL_ID = "Payment Service";
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);

        NotificationCompat.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, getString(R.string.notification_service_channel),
                    NotificationManager.IMPORTANCE_DEFAULT);
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);
            builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        } else {
            builder = new NotificationCompat.Builder(this);
        }
        builder.setAutoCancel(false)
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_stat_card)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.foreground_service_message))
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .build();
        return builder.build();
    }

}
