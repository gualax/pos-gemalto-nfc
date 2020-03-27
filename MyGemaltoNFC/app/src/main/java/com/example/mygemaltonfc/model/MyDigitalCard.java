package com.example.mygemaltonfc.model;

import com.gemalto.mfs.mwsdk.dcm.DigitalizedCard;
import com.gemalto.mfs.mwsdk.dcm.DigitalizedCardStatus;

public class MyDigitalCard {

    private String tokenId;
    private String digitalizedCardId;
    private boolean isDefaultCard;
    private DigitalizedCardStatus cardStatus;

    public MyDigitalCard(DigitalizedCard card) {
        this.tokenId = card.getTokenizedCardID();
    }


    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public String getDigitalizedCardId() {
        return digitalizedCardId;
    }

    public void setDigitalizedCardId(String digitalizedCardId) {
        this.digitalizedCardId = digitalizedCardId;
    }

    public boolean isDefaultCard() {
        return isDefaultCard;
    }

    public void setDefaultCard(boolean defaultCard) {
        isDefaultCard = defaultCard;
    }

    public DigitalizedCardStatus getCardStatus() {
        return cardStatus;
    }

    public void setCardStatus(DigitalizedCardStatus cardStatus) {
        this.cardStatus = cardStatus;
    }
}
