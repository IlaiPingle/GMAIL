package com.example.MyGmail.data.remote.net;

import android.content.Context;
import android.net.Uri;

import com.example.MyGmail.data.models.Label;
import com.example.MyGmail.data.remote.api.WebServiceAPI;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import retrofit2.Callback;

/**
 * Client class for interacting with the Label API.
 * It provides methods to fetch, create, update, and delete labels using Retrofit.
 * The class uses the WebServiceAPI interface to define the API endpoints.
 * Each method accepts a Retrofit Callback to handle asynchronous responses.
 */
public class LabelAPIClient {
    private final WebServiceAPI labelApi;

    public LabelAPIClient(Context context) {
        this.labelApi = ApiClient.getClient(context.getApplicationContext()).create(WebServiceAPI.class);
    }

    private static String enc(String s) {
        return s == null ? "" : Uri.encode(s, null);
    }

    public void getLabels(Callback<List<String>> callback) {
        labelApi.getLabels().enqueue(callback);
    }

    public void getLabelByName(String labelName, Callback<Label> callback) {
        labelApi.getLabelByName(enc(labelName)).enqueue(callback);
    }
    public void createLabel(String labelName, Callback<Label> callback) {
        labelApi.createLabel(Collections.singletonMap("labelName", labelName)).enqueue(callback);
    }

    public void updateLabel(String oldName, String newName, Callback<Void> callback) {
        String trimmed = newName == null ? "" : newName.trim();
        Map<String, String> body = Collections.singletonMap("newName", trimmed);
        labelApi.updateLabel(enc(oldName), body).enqueue(callback);
    }

    public void deleteLabel(String labelName, Callback<Void> callback) {
        labelApi.deleteLabel(enc(labelName)).enqueue(callback);
    }
}
