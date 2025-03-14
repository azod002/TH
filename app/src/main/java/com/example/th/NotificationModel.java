package com.example.th;

import com.google.firebase.Timestamp;

public class NotificationModel {
    private String senderUid;
    private String receiverUid;
    private String message;
    private Timestamp scheduledTime;
    private Timestamp createdAt;

    // Обязательный конструктор без параметров для Firestore
    public NotificationModel() {
    }

    public NotificationModel(String senderUid, String receiverUid, String message, Timestamp scheduledTime, Timestamp createdAt) {
        this.senderUid = senderUid;
        this.receiverUid = receiverUid;
        this.message = message;
        this.scheduledTime = scheduledTime;
        this.createdAt = createdAt;
    }

    public String getSenderUid() {
        return senderUid;
    }

    public String getReceiverUid() {
        return receiverUid;
    }

    public String getMessage() {
        return message;
    }

    public Timestamp getScheduledTime() {
        return scheduledTime;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }
}
