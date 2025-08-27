package com.example.androidproject.model;

import java.util.List;

/**
 * Model class representing the response from the email API.
 * Contains a list of emails and metadata about the response.
 */
public class EmailResponse {
    private List<EmailData> emails;
    private boolean success;
    private String message;

    // Getters and setters
    public List<EmailData> getEmails() { return emails; }
    public void setEmails(List<EmailData> emails) { this.emails = emails; }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}