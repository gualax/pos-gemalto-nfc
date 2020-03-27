package com.example.mygemaltonfc.hce;

import com.gemalto.mfs.mwsdk.payment.AbstractHCEService;
import com.gemalto.mfs.mwsdk.payment.PaymentServiceListener;

/*
* HCEService creacion alan
* AbstractHCEService  extension SDK Gemalto
* */

public class HCEService extends AbstractHCEService {
    @Override
    public PaymentServiceListener setupListener() {
        return null;
    }

    @Override
    public void setupPluginRegistration() {

    }

    @Override
    public boolean setupCardActivation() {
        return false;
    }
}
