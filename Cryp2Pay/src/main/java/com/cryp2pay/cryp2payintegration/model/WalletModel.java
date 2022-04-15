package com.cryp2pay.cryp2payintegration.model;

public class WalletModel {
    String coin, value, inr;

    public WalletModel(){

    }

    public WalletModel(String coin, String value, String inr) {
        this.coin = coin;
        this.value = value;
        this.inr = inr;
    }

    public String getCoin() {
        return coin;
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getInr() {
        return inr;
    }

    public void setInr(String inr) {
        this.inr = inr;
    }
}
