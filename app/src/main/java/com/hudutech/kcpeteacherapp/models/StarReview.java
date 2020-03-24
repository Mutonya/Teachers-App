package com.hudutech.kcpeteacherapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import javax.annotation.Nullable;

public class StarReview implements Parcelable {
    private String docKey;
    private String reviewedUserId;
    private String reviewByUid;
    @Nullable
    private String review;
    private float stars;
    private long timestamp;

    public StarReview() {}

    public StarReview(String docKey, String reviewedUserId, String reviewByUid, @Nullable String review, float stars, long timestamp) {
        this.docKey = docKey;
        this.reviewedUserId = reviewedUserId;
        this.reviewByUid = reviewByUid;
        this.review = review;
        this.stars = stars;
        this.timestamp = timestamp;
    }

    protected StarReview(Parcel in) {
        docKey = in.readString();
        reviewedUserId = in.readString();
        reviewByUid = in.readString();
        review = in.readString();
        stars = in.readFloat();
        timestamp = in.readLong();
    }

    public static final Creator<StarReview> CREATOR = new Creator<StarReview>() {
        @Override
        public StarReview createFromParcel(Parcel in) {
            return new StarReview(in);
        }

        @Override
        public StarReview[] newArray(int size) {
            return new StarReview[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(docKey);
        dest.writeString(reviewedUserId);
        dest.writeString(reviewByUid);
        dest.writeString(review);
        dest.writeFloat(stars);
        dest.writeLong(timestamp);
    }

    public String getDocKey() {
        return docKey;
    }

    public void setDocKey(String docKey) {
        this.docKey = docKey;
    }

    public String getReviewedUserId() {
        return reviewedUserId;
    }

    public void setReviewedUserId(String reviewedUserId) {
        this.reviewedUserId = reviewedUserId;
    }

    public String getReviewByUid() {
        return reviewByUid;
    }

    public void setReviewByUid(String reviewByUid) {
        this.reviewByUid = reviewByUid;
    }

    @Nullable
    public String getReview() {
        return review;
    }

    public void setReview(@Nullable String review) {
        this.review = review;
    }

    public float getStars() {
        return stars;
    }

    public void setStars(float stars) {
        this.stars = stars;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "StarReview{" +
                "docKey='" + docKey + '\'' +
                ", reviewedUserId='" + reviewedUserId + '\'' +
                ", reviewByUid='" + reviewByUid + '\'' +
                ", review='" + review + '\'' +
                ", stars=" + stars +
                ", timestamp=" + timestamp +
                '}';
    }
}
