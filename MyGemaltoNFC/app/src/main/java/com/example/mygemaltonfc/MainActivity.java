package com.example.mygemaltonfc;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.mygemaltonfc.fragments.AddCardFragment;
import com.example.mygemaltonfc.fragments.ListCardsFragment;
import com.example.mygemaltonfc.hce.ContactLessPaymentService;
import com.example.mygemaltonfc.sdk.SDKHelper;
import com.example.mygemaltonfc.sdk.SDKService;
import com.gemalto.mfs.mwsdk.mobilegateway.MGConfigurationChangeReceiver;
import com.gemalto.mfs.mwsdk.mobilegateway.MobileGatewayError;
import com.gemalto.mfs.mwsdk.mobilegateway.MobileGatewayManager;
import com.gemalto.mfs.mwsdk.mobilegateway.enrollment.IDVMethod;
import com.gemalto.mfs.mwsdk.mobilegateway.enrollment.IDVMethodSelector;
import com.gemalto.mfs.mwsdk.mobilegateway.enrollment.PendingCardActivation;
import com.gemalto.mfs.mwsdk.mobilegateway.enrollment.PendingCardActivationState;
import com.gemalto.mfs.mwsdk.mobilegateway.exception.MGConfigurationException;
import com.gemalto.mfs.mwsdk.mobilegateway.listener.MGDigitizationListener;
import com.gemalto.mfs.mwsdk.provisioning.ProvisioningServiceManager;
import com.gemalto.mfs.mwsdk.provisioning.listener.EnrollingServiceListener;
import com.gemalto.mfs.mwsdk.provisioning.model.EnrollmentStatus;
import com.gemalto.mfs.mwsdk.provisioning.model.ProvisioningServiceError;
import com.gemalto.mfs.mwsdk.provisioning.sdkconfig.EnrollingBusinessService;
import com.gemalto.mfs.mwsdk.provisioning.sdkconfig.ProvisioningBusinessService;
import com.gemalto.mfs.mwsdk.utils.chcodeverifier.CHCodeVerifier;
import com.gemalto.mfs.mwsdk.utils.chcodeverifier.SecureCodeInputer;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;

public class MainActivity extends AppCompatActivity implements MGDigitizationListener, EnrollingServiceListener {


    private ContactLessPaymentService contactlessPayListener;
    private static final String TAG = "MainActivity";
    byte[] activationCode;
    private MGConfigurationChangeReceiver configurationChangeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e("MainActivity","************** STARTING MAIN ACTIVITY ************");

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container, new AddCardFragment());
        ft.commit();

        //////////   init //////////
        contactlessPayListener = new ContactLessPaymentService(this.getApplication());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(this, SDKService.class));
        }else {
            startService(new Intent(this, SDKService.class));
        }

        FirebaseApp.initializeApp(this);
        //trigger FCM token fetching to be able to get token readily
        FirebaseInstanceId.getInstance().getToken();

        //init MG SDK
        try {
            SDKHelper.initMGSDK(this);
        } catch (MGConfigurationException e) {
            e.printStackTrace();
        }
        //set up sync between MG and CPS
        configurationChangeReceiver = new MGConfigurationChangeReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(configurationChangeReceiver,
                new IntentFilter("com.gemalto.mfs.action.MGConfigurationChanged"));
        ////////// FIN  init //////////

    }

    public void switchFragment(ListCardsFragment fragment, boolean b) {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
    }



    ///  Interface implementation /////
    @Override
    public void onCPSActivationCodeAcquired(String id, byte[] code) {
//TODO: Trigger CPS Enrollment
        Toast.makeText(this, ".onCPSActivationCodeAcquired() :", Toast.LENGTH_SHORT).show();

        String firebaseToken = FirebaseInstanceId.getInstance().getToken();
        if (firebaseToken == null) {
            throw new RuntimeException("Firebase token is null ");
        }
        Log.d(TAG, "Firebase token is " + firebaseToken);
        EnrollingBusinessService enrollingService = ProvisioningServiceManager.getEnrollingBusinessService();
        ProvisioningBusinessService provisioningBusinessService = ProvisioningServiceManager.getProvisioningBusinessService();

        this.activationCode = new byte[code.length];
        for (int i = 0; i < code.length; i++) {
            activationCode[i] = code[i];
        }

        //WalletID of MG SDK is userID of CPS SDK Enrollment process
        String userId = MobileGatewayManager.INSTANCE.getCardEnrollmentService().getWalletId();
        Log.e(TAG, "Wallet ID : "+userId);
        EnrollmentStatus status = enrollingService.isEnrolled();
        switch (status) {
            case ENROLLMENT_NEEDED:
                enrollingService.enroll(userId, firebaseToken, "en", this);
                break;
            case ENROLLMENT_IN_PROGRESS:
                enrollingService.continueEnrollment("en", this);
                break;
            case ENROLLMENT_COMPLETE:
                provisioningBusinessService.sendActivationCode(this);
                break;
        }


    }

    @Override
    public void onSelectIDVMethod(IDVMethodSelector idvMethodSelector) {
        Toast.makeText(this, ".onSelectIDVMethod() :", Toast.LENGTH_SHORT).show();
        //For demo purpose, we skip and select the first one
        IDVMethod firstMethod = idvMethodSelector.getIdvMethodList()[0];
        idvMethodSelector.select(firstMethod.getId());
    }

    @Override
    public void onActivationRequired(PendingCardActivation pendingCardActivation) {
        Toast.makeText(this, ".onActivationRequired() :", Toast.LENGTH_SHORT).show();
        if (pendingCardActivation.getState() == PendingCardActivationState.WEB_3DS_NEEDED) {

        } else if(pendingCardActivation.getState() == PendingCardActivationState.OTP_NEEDED){
            //TODO -  display uI to collect  OTP.
            //TODO - Implement asking for OTP. For demo, there is no environment setup.
            pendingCardActivation.activate("otp collected".getBytes(), this);
        }
    }

    @Override
    public void onComplete(String s) {
        Toast.makeText(this, "Digitize Successful : " + s, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onError(String s, MobileGatewayError mobileGatewayError) {
        Toast.makeText(this, mobileGatewayError.getMessage(), Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onCodeRequired(CHCodeVerifier chCodeVerifier) {
        Log.d(TAG, ".onCodeRequired called. Providing activation code");
        SecureCodeInputer inputer = chCodeVerifier.getSecureCodeInputer();
        for (byte i : activationCode) {
            inputer.input(i);
        }
        inputer.finish();

        //wipe after use
        for (int i = 0; i < activationCode.length; i++) {
            activationCode[i] = 0;
        }
    }

    @Override
    public void onStarted() {
        Log.d(TAG, ".onStarted()");
    }

    @Override
    public void onError(ProvisioningServiceError provisioningServiceError) {
        Log.e(TAG, ".onError() - " + provisioningServiceError.getErrorMessage());

    }

    @Override
    public void onComplete() {
        Log.d(TAG, ".onComplete()");
        Toast.makeText(this,"onComplete!!!!!", Toast.LENGTH_SHORT).show();

    }
}
