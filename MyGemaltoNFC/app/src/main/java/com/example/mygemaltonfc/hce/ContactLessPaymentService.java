package com.example.mygemaltonfc.hce;

import android.app.Application;
import android.content.Context;

import com.gemalto.mfs.mwsdk.payment.CHVerificationMethod;
import com.gemalto.mfs.mwsdk.payment.PaymentServiceErrorCode;
import com.gemalto.mfs.mwsdk.payment.engine.ContactlessPaymentServiceListener;
import com.gemalto.mfs.mwsdk.payment.engine.PaymentService;
import com.gemalto.mfs.mwsdk.payment.engine.TransactionContext;

/*
 * ContactLessPaymentService creacion alan
 * ContactlessPaymentServiceListener  interfaz SDK Gemalto
 * */

public class ContactLessPaymentService implements ContactlessPaymentServiceListener {
private Context context;

    public ContactLessPaymentService(Application app) {
        this.context = app.getApplicationContext();
    }


    @Override
    public void onReadyToTap(PaymentService paymentService) {

    }

    @Override
    public void onTransactionCompleted(TransactionContext transactionContext) {

    }

    @Override
    public void onPaymentStarted() {

    }

    @Override
    public void onPaymentServiceActivated(PaymentService paymentService, CHVerificationMethod chVerificationMethod, long l) {

    }

    @Override
    public void onError(TransactionContext transactionContext, PaymentServiceErrorCode paymentServiceErrorCode, String s) {

    }
}
