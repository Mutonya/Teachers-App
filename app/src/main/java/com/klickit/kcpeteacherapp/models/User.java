package com.klickit.kcpeteacherapp.models;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {


    private String userId;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String avatarUrl;
    private String county;
    private String school;
    private String userType;
    private String deviceToken;

    public User() {
    }


    public User(String userId, String firstName, String lastName, String email, String phoneNumber, String avatarUrl, String deviceToken, String county, String school, String accountType) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = firstName + " " + lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.avatarUrl = avatarUrl;
        this.deviceToken = deviceToken;
        this.county = county;
        this.userType = accountType;
        this.school = school;
    }

    protected User(Parcel in) {
        userId = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        fullName = in.readString();
        email = in.readString();
        phoneNumber = in.readString();
        avatarUrl = in.readString();
        county = in.readString();
        school = in.readString();
        userType = in.readString();
        deviceToken = in.readString();
    }


    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", county='" + county + '\'' +
                ", school='" + school + '\'' +
                ", userType='" + userType + '\'' +
                ", deviceToken='" + deviceToken + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(fullName);
        dest.writeString(email);
        dest.writeString(phoneNumber);
        dest.writeString(avatarUrl);
        dest.writeString(county);
        dest.writeString(school);
        dest.writeString(userType);
        dest.writeString(deviceToken);
    }
}
