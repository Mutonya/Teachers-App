package com.hudutech.kcpeteacherapp.models;


import androidx.annotation.NonNull;

public class Conversation {
    private boolean seen;
    private long timestamp;
    private String docKey;

    public Conversation() {
    }

    public Conversation(boolean seen, long timestamp, String docKey) {
        this.seen = seen;
        this.timestamp = timestamp;
        this.docKey = docKey;
    }

    public boolean isSeen() {
        return seen;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getDocKey() {
        return docKey;
    }

    public void setDocKey(String docKey) {
        this.docKey = docKey;
    }

    @NonNull
    @Override
    public String toString() {
        return "Conversation{" +
                "seen=" + seen +
                ", timestamp=" + timestamp +
                ", docKey='" + docKey + '\'' +
                '}';
    }
}
