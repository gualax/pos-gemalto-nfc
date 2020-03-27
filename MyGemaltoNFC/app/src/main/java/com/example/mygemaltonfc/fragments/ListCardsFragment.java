package com.example.mygemaltonfc.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mygemaltonfc.MainActivity;
import com.example.mygemaltonfc.R;
import com.example.mygemaltonfc.adapters.CardsAdapter;
import com.example.mygemaltonfc.model.Card;
import com.gemalto.mfs.mwsdk.mobilegateway.MGCardEnrollmentService;
import com.gemalto.mfs.mwsdk.mobilegateway.MobileGatewayError;
import com.gemalto.mfs.mwsdk.mobilegateway.MobileGatewayManager;
import com.gemalto.mfs.mwsdk.mobilegateway.enrollment.IDVMethodSelector;
import com.gemalto.mfs.mwsdk.mobilegateway.enrollment.PendingCardActivation;
import com.gemalto.mfs.mwsdk.mobilegateway.enrollment.TermsAndConditions;
import com.gemalto.mfs.mwsdk.mobilegateway.listener.MGDigitizationListener;
import com.gemalto.mfs.mwsdk.provisioning.ProvisioningServiceManager;
import com.gemalto.mfs.mwsdk.provisioning.model.EnrollmentStatus;
import com.gemalto.mfs.mwsdk.provisioning.sdkconfig.EnrollingBusinessService;
import com.gemalto.mfs.mwsdk.provisioning.sdkconfig.ProvisioningBusinessService;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import java.util.ArrayList;

public class ListCardsFragment extends Fragment {

    CardsAdapter mCardListAdapter;
    RecyclerView mRecyclerView;
    TermsAndConditions termsAndConditions;
    Card card;
    private static final String TAG =  "ListCardsFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.card_list,container,false);
        ArrayList<Card> cards = new ArrayList<>();
        cards.add(card);

        mRecyclerView = rootView.findViewById(R.id.card_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false));

        proceedDigitize();
        //showCardData(cards);

        return rootView;
    }


    public void showCardData(ArrayList<Card> cards) {
        mCardListAdapter = new CardsAdapter(getContext(),cards);
        mRecyclerView.setAdapter(mCardListAdapter);
    }


    private void proceedDigitize() {
        Log.e(TAG,"******* Process digitize ********");
        MGCardEnrollmentService enrollmentService  = MobileGatewayManager.INSTANCE.getCardEnrollmentService();
        enrollmentService.digitizeCard(termsAndConditions.accept(), null, null,((MainActivity)getActivity()));
    }
}
