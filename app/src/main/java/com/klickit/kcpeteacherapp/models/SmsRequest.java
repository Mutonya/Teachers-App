package com.klickit.kcpeteacherapp.models;

import com.google.gson.annotations.SerializedName;

import static com.klickit.kcpeteacherapp.api.ApiConstants.SMS_API_KEY;
import static com.klickit.kcpeteacherapp.api.ApiConstants.SMS_PARTNER_ID;
import static com.klickit.kcpeteacherapp.api.ApiConstants.SMS_SENDER_ID;


public class SmsRequest {

    @SerializedName("partnerID")
    private String partnerId;
    @SerializedName("apikey")
    private String apiKey;
    @SerializedName("mobile")
    private String phoneNumber;
    @SerializedName("message")
    private String message;
    @SerializedName("shortcode")
    private String shortCode;
    @SerializedName("pass_type")
    private String passType;

    public SmsRequest(String phoneNumber, String message) {
        this.phoneNumber = phoneNumber;
        this.message = message;
        this.partnerId = SMS_PARTNER_ID;
        this.shortCode = SMS_SENDER_ID;
        this.apiKey = SMS_API_KEY;
        this.passType = "plain";
    }

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public String getPassType() {
        return passType;
    }

    public void setPassType(String passType) {
        this.passType = passType;
    }

    @Override
    public String toString() {
        return "SmsRequest{" +
                "partnerId='" + partnerId + '\'' +
                ", apiKey='" + apiKey + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", message='" + message + '\'' +
                ", shortCode='" + shortCode + '\'' +
                ", passType='" + passType + '\'' +
                '}';
    }
}
