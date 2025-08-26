// EmailItem.java
package com.example.androidproject;

public class EmailItem {
    private String sender;
    private String subject;
    private String preview;
    private String time;

    public EmailItem(String sender, String subject, String preview, String time) {
        this.sender = sender;
        this.subject = subject;
        this.preview = preview;
        this.time = time;
    }

    // Getters
    public String getSender() { return sender; }
    public String getSubject() { return subject; }
    public String getPreview() { return preview; }
    public String getTime() { return time; }
}