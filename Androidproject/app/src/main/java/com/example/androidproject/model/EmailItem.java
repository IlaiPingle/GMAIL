package com.example.androidproject.model;

/**
 * Model class representing an individual email item.
 * Used in the RecyclerView adapter for displaying emails.
 */
public class EmailItem {
    private String sender;
    private String subject;
    private String preview;
    private String time;
    private boolean isRead;
    private boolean isStarred;

    // Constructor
    public EmailItem(String sender, String subject, String preview, String time, boolean isRead) {
        this.sender = sender;
        this.subject = subject;
        this.preview = preview;
        this.time = time;
        this.isRead = isRead;
        this.isStarred = false; // Default to not starred
    }

    // Getters and setters
    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getPreview() { return preview; }
    public void setPreview(String preview) { this.preview = preview; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }

    public boolean isStarred() { return isStarred; }
    public void setStarred(boolean starred) { isStarred = starred; }
}