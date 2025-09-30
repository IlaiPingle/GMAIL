package com.example.androidproject.data.remote.net;


import android.content.Context;

import com.example.androidproject.data.models.Mail;
import com.example.androidproject.data.remote.api.WebServiceAPI;

import java.util.Collections;
import java.util.List;
import retrofit2.Callback;

public class MailAPIClient {

    private final WebServiceAPI mailApi;

    public MailAPIClient(Context ctx) {
        Context context = ctx.getApplicationContext();
        this.mailApi = ApiClient.getClient(context).create(WebServiceAPI.class);
    }

    public void getMails(Callback<List<Mail>> callback) {
        mailApi.getMails().enqueue(callback);
    }

    public void createDraft(Mail mail, Callback<Mail> callback) {
        mailApi.createDraft(mail).enqueue(callback);
    }

    public void sendMail(String id, Mail mail, Callback<Mail> callback) {
        mailApi.sendMail(id, mail).enqueue(callback);
    }

    public void updateMail(String id, Mail mail, Callback<Void> callback) {
        mailApi.updateMail(id, mail).enqueue(callback);
    }

    public void deleteMail(String id, Callback<Void> callback) {
        mailApi.deleteMail(id).enqueue(callback);
    }

    public void searchMails(String query, Callback<List<Mail>> callback) {
        mailApi.searchMails(query).enqueue(callback);
    }

    public void getMailById(String id, Callback<Mail> callback) {
        mailApi.getMailById(id).enqueue(callback);
    }

    //labels filtering
    public void getMailsByLabel(String label, Callback<List<Mail>> callback) {
        mailApi.getMailsByLabel(label).enqueue(callback);
    }

    public void addLabelToMail(String mailId, String labelName, Callback<Void> callback) {
        mailApi.addLabelToMail(mailId, Collections.singletonMap("labelName", labelName)).enqueue(callback);
    }

    public void removeLabelFromMail(String mailId, String labelName, Callback<Void> callback) {
        mailApi.removeLabelFromMail(mailId, Collections.singletonMap("labelName", labelName)).enqueue(callback);
    }

    public void addToBlacklist(String url, Callback<Void> callback) {
        mailApi.addToBlacklist(Collections.singletonMap("url", url)).enqueue(callback);
    }
    public void removeFromBlacklist(String url, Callback<Void> callback) {
        mailApi.removeFromBlacklist(url).enqueue(callback);
    }
}
