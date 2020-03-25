package com.hudutech.kcpeteacherapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class ChatMessage implements Parcelable {
    public static final Creator<ChatMessage> CREATOR = new Creator<ChatMessage>() {
        @Override
        public ChatMessage createFromParcel(Parcel in) {
            return new ChatMessage(in);
        }

        @Override
        public ChatMessage[] newArray(int size) {
            return new ChatMessage[size];
        }
    };
    private String fromUid;
    private String toUid;
    private String message;
    private boolean seen;
    private long sentOn;

    public ChatMessage() {
    }

    public ChatMessage(String fromUid, String toUid, String message, boolean seen, long sentOn) {
        this.fromUid = fromUid;
        this.toUid = toUid;
        this.message = message;
        this.seen = seen;
        this.sentOn = sentOn;
    }

    @SuppressWarnings("WeakerAccess")
    protected ChatMessage(Parcel in) {
        fromUid = in.readString();
        toUid = in.readString();
        message = in.readString();
        seen = in.readByte() != 0;
        sentOn = in.readLong();
    }

    public String getFromUid() {
        return fromUid;
    }

    public String getToUid() {
        return toUid;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSeen() {
        return seen;
    }

    public long getSentOn() {
        return sentOn;
    }

    public String formattedTime() {
        SimpleDateFormat df = new SimpleDateFormat("hh:mm", Locale.getDefault());
        return df.format(this.getSentOn());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(fromUid);
        parcel.writeString(toUid);
        parcel.writeString(message);
        parcel.writeByte((byte) (seen ? 1 : 0));
        parcel.writeLong(sentOn);
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "fromUid='" + fromUid + '\'' +
                ", toUid='" + toUid + '\'' +
                ", message='" + message + '\'' +
                ", seen=" + seen +
                ", sentOn=" + sentOn +
                '}';
    }
}
