package com.cryp2pay.cryp2payintegration.model;

public class BasicInfoModel {
    String merchantName, merchantPhone, merchantID;

    public BasicInfoModel(String merchantName, String merchantPhone, String merchantID) {
        this.merchantName = merchantName;
        this.merchantPhone = merchantPhone;
        this.merchantID = merchantID;
    }

    public BasicInfoModel(){

    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getMerchantPhone() {
        return merchantPhone;
    }

    public void setMerchantPhone(String merchantPhone) {
        this.merchantPhone = merchantPhone;
    }

    public String getMerchantID() {
        return merchantID;
    }

    public void setMerchantID(String merchantID) {
        this.merchantID = merchantID;
    }
}
