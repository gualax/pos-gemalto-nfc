package com.example.mygemaltonfc.fragments;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mygemaltonfc.MainActivity;
import com.example.mygemaltonfc.R;
import com.example.mygemaltonfc.hce.ContactLessPaymentService;
import com.example.mygemaltonfc.model.Card;
import com.example.mygemaltonfc.sdk.SDKHelper;
import com.example.mygemaltonfc.utils.Constants;
import com.gemalto.mfs.mwsdk.mobilegateway.MGCardEnrollmentService;
import com.gemalto.mfs.mwsdk.mobilegateway.MobileGatewayError;
import com.gemalto.mfs.mwsdk.mobilegateway.MobileGatewayManager;
import com.gemalto.mfs.mwsdk.mobilegateway.enrollment.InputMethod;
import com.gemalto.mfs.mwsdk.mobilegateway.enrollment.IssuerData;
import com.gemalto.mfs.mwsdk.mobilegateway.enrollment.TermsAndConditions;
import com.gemalto.mfs.mwsdk.mobilegateway.exception.MGConfigurationException;
import com.gemalto.mfs.mwsdk.mobilegateway.listener.CardEligibilityListener;
import com.gemalto.mfs.mwsdk.mobilegateway.listener.MGDigitizationListener;
import com.gemalto.mfs.mwsdk.mobilegateway.utils.MGCardInfoEncryptor;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;


import static androidx.navigation.Navigation.findNavController;

public class AddCardFragment extends   Fragment implements CardEligibilityListener {

    Button addBtn;
    EditText etCardNumber, etCardCvv, etCardHolder, etCardExpDate;
    private ContactLessPaymentService contactlessPayListener;
    private static final String TAG = "AddCardFragment";
    private String cardStringArg;
    private View rootView;
    private Card enrolledCard;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         rootView  = inflater.inflate(R.layout.add_card_fragment,container,false);


        addBtn = rootView.findViewById(R.id.btn_add_card);
        etCardNumber = rootView.findViewById(R.id.t_frag_card_number);
        etCardCvv = rootView.findViewById(R.id.t_card_cvv);
        etCardExpDate = rootView.findViewById(R.id.t_card_exp_date);
        etCardHolder = rootView.findViewById(R.id.t_card_holder);


        etCardNumber.setText("4622943127006808");
        etCardExpDate.setText("1220");
        etCardCvv.setText("123");


        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               collectDataFromUI();
            }
        });

        return rootView;
    }


    public void collectDataFromUI(){
        Log.e(TAG," ****  collectDataFromUI *** ");

        Card card = new Card();
        if (etCardNumber.getText().length() < 16) {
            Toast.makeText(getActivity(), R.string.invalid_pan, Toast.LENGTH_SHORT).show();
        }

        if (etCardExpDate.getText().length() < 4) {
            Toast.makeText(getActivity(), R.string.invalid_expiry, Toast.LENGTH_SHORT).show();
        }

        if (etCardCvv.getText().length() < 3) {
            Toast.makeText(getActivity(), R.string.invalid_cvv, Toast.LENGTH_SHORT).show();
        }

        card.setExpDate(etCardExpDate.getText().toString());
        card.setNumber(etCardNumber.getText().toString());
        card.setHolder(etCardHolder.getText().toString());
        card.setCvv(Integer.valueOf(etCardCvv.getText().toString()));
        enrolledCard = card;
        enrrollCardService(card);
    }

    public void enrrollCardService(Card card) {
        Log.e(TAG," ****  enrrollCardService *** ");

        byte[] pubKeyBytes = MGCardInfoEncryptor.parseHex(Constants.DEBUG_PUBLIC_KEY_VTS_LAB);
        byte[] subKeyBytes = MGCardInfoEncryptor.parseHex(Constants.DEBUG_SUBJECT_IDENTIFIER_VTS_LAB);
        byte[] panBytes = card.getNumber().trim().replace(" ", "").getBytes();
        byte[] expBytes = card.getExpDate().getBytes();
        byte[] cvvBytes = String.valueOf(card.getCvv()).getBytes();
        byte[] encData = MGCardInfoEncryptor.encrypt(pubKeyBytes, subKeyBytes,
                panBytes, expBytes, cvvBytes);

        MGCardEnrollmentService enrollmentService = MobileGatewayManager.INSTANCE.getCardEnrollmentService();
        //InputMethod.BANK_APP is required for GreenFlow
        enrollmentService.checkCardEligibility(encData, InputMethod.MANUAL, "en", this, getDeviceSerial());
    }

    private String getDeviceSerial() {
        return Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
    }


    @Override
    public void onSuccess(TermsAndConditions termsAndConditions, IssuerData issuerData) {
         Log.e(TAG," ****  enrollmentService onSuccess *** ");
         ListCardsFragment frag = new ListCardsFragment();
        frag.termsAndConditions = termsAndConditions;
        frag.card = enrolledCard;
        ((MainActivity) getActivity()).switchFragment(frag, true);
    //     findNavController(rootView).navigate(AddCardFragmentDirections.toCardList(enrolledCard.toString()));
    }

    @Override
    public void onError(MobileGatewayError mobileGatewayError) {
        Log.e(TAG,"*** enrollmentService onError **** ");
        Log.e(TAG,mobileGatewayError.getMessage());
    }

}
