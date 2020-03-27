package com.klickit.kcpeteacherapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class ChatMessage implements Parcelable {

    private String fromUid;
    private String toUid;
    private String message;
    private String mediaType;
    private boolean seen;
    private long sentOn;

    public ChatMessage() {
    }



    public ChatMessage(String fromUid, String toUid, String message, String mediaType, boolean seen, long sentOn) {
        this.fromUid = fromUid;
        this.toUid = toUid;
        this.message = message;
        this.mediaType = mediaType;
        this.seen = seen;
        this.sentOn = sentOn;
    }

    protected ChatMessage(Parcel in) {
        fromUid = in.readString();
        toUid = in.readString();
        message = in.readString();
        mediaType = in.readString();
        seen = in.readByte() != 0;
        sentOn = in.readLong();
    }

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

    public String getFromUid() {
        return fromUid;
    }

    public void setFromUid(String fromUid) {
        this.fromUid = fromUid;
    }

    public String getToUid() {
        return toUid;
    }

    public void setToUid(String toUid) {
        this.toUid = toUid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public long getSentOn() {
        return sentOn;
    }

    public void setSentOn(long sentOn) {
        this.sentOn = sentOn;
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
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fromUid);
        dest.writeString(toUid);
        dest.writeString(message);
        dest.writeString(mediaType);
        dest.writeByte((byte) (seen ? 1 : 0));
        dest.writeLong(sentOn);
    }


    @Override
    public String toString() {
        return "ChatMessage{" +
                "fromUid='" + fromUid + '\'' +
                ", toUid='" + toUid + '\'' +
                ", message='" + message + '\'' +
                ", mediaType='" + mediaType + '\'' +
                ", seen=" + seen +
                ", sentOn=" + sentOn +
                '}';
    }
}
