package com.example.mygemaltonfc.model;

import com.google.gson.Gson;

public class Card {

    private String number;
    private String holder;
    private String expDate;
    private int cvv;
    private String emisorBank;
    private String emisorIdentity;


    public Card() {
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getHolder() {
        return holder;
    }

    public void setHolder(String holder) {
        this.holder = holder;
    }

    public String getExpDate() {
        return expDate;
    }

    public void setExpDate(String expDate) {
        this.expDate = expDate;
    }

    public int getCvv() {
        return cvv;
    }

    public void setCvv(int cvv) {
        this.cvv = cvv;
    }

    public String getEmisorBank() {
        return emisorBank;
    }

    public void setEmisorBank(String emisorBank) {
        this.emisorBank = emisorBank;
    }

    public String getEmisorIdentity() {
        return emisorIdentity;
    }

    public void setEmisorIdentity(String emisorIdentity) {
        this.emisorIdentity = emisorIdentity;
    }

    public String toString(){
        Gson gson = new Gson();
        String jsonString = gson.toJson(this);
        return jsonString;
    }


}
