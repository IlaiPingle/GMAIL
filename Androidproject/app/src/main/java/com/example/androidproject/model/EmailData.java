package com.example.androidproject.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;
/**
 * Model class representing an individual email item.
 * Maps to the JSON structure returned by the email API.
 */
public class EmailData {
    private String id;
    private String sender;
    private String receiver;
    private String subject;
    private String body;
    private String preview;
    private boolean read;
    private boolean starred;
    private List<String> labels;
    @SerializedName("createdAt")
    private String createdAt;

    // Getters and setters
    public String getId() { return id; }
    public String getSender() { return sender; }
    public String getReceiver() { return receiver; }
    public String getSubject() { return subject; }
    public String getBody() { return body; }
    public String getPreview() { return preview; }
    public boolean isRead() { return read; }
    public boolean isStarred() { return starred; }
    public List<String> getLabels() { return labels; }
    public String getCreatedAt() { return createdAt; }

    // Convert to app model
    public EmailItem toEmailItem() {
        String preview = body == null ? "" : (body.length() > 60 ? body.substring(0, 60) + "..." : body);
        String time = (createdAt == null) ? "" : createdAt.replace('T', ' ').replace('Z', ' ');
        boolean read = labels != null && !labels.contains("unread");
        boolean starred = labels != null && labels.contains("starred");
        EmailItem item = new EmailItem(sender, subject, preview, time, read);
        item.setStarred(starred);
        return item;
    }
}