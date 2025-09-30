package com.example.androidproject.data.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


import java.util.List;
import java.util.Objects;

@Entity(tableName = "mails")
public class Mail {
    @PrimaryKey
    @NonNull
    private String id;
    private String sender;
    private String receiver;
    private String subject;
    private String body;
    private List<String> labels;
    private String createdAt;

    // Getters and setters


    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sender, receiver, subject, body, createdAt, labels != null ? labels.size() : 0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Mail)) return false;
        Mail mail = (Mail) o;

        return Objects.equals(id, mail.id) &&
                Objects.equals(sender, mail.sender) &&
                Objects.equals(receiver, mail.receiver) &&
                Objects.equals(subject, mail.subject) &&
                Objects.equals(body, mail.body) &&
                Objects.equals(createdAt, mail.createdAt) &&
                ((labels == null && mail.labels == null) ||
                        (labels != null && mail.labels != null &&
                                labels.size() == mail.labels.size() &&
                                labels.containsAll(mail.labels)));
    }
}