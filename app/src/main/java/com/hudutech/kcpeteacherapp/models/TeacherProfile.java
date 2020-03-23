package com.hudutech.kcpeteacherapp.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.List;
@Entity
@TypeConverters({SubjectTypeConverter.class})
public class TeacherProfile {
    @PrimaryKey
    @ColumnInfo(name = "user_id")
    private String userId;
    @ColumnInfo(name = "first_name")
    private String firstName;
    @ColumnInfo(name = "last_name")
    private String lastName;
    @ColumnInfo(name = "display_name")
    private String displayName;
    @ColumnInfo(name = "email")
    private String email;
    @ColumnInfo(name = "phone_number")
    private String phoneNumber;
    @ColumnInfo(name = "salutation")
    private String salutation;
    @ColumnInfo(name = "avatarUrl")
    private String avatarUrl;
    @ColumnInfo(name = "school")
    private String school;
    private List<String> subjects;
    @ColumnInfo(name = "approved")
    private boolean approved;

    public TeacherProfile(){}

    @Ignore
    public TeacherProfile(String userId, String firstName, String lastName, String phoneNumber, String email,  String salutation, String avatarUrl, String school, List<String> subjects) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.displayName = String.format("%s %s", salutation, lastName);
        this.salutation = salutation;
        this.avatarUrl = avatarUrl;
        this.school = school;
        this.subjects = subjects;
    }


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

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getSalutation() {
        return salutation;
    }

    public void setSalutation(String salutation) {
        this.salutation = salutation;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public List<String> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<String> subjects) {
        this.subjects = subjects;
    }
}

