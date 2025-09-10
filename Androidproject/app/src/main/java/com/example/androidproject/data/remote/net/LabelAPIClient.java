package com.example.androidproject.data.remote.net;

import android.content.Context;

import com.example.androidproject.data.local.db.MyApplication;
import com.example.androidproject.data.models.Label;
import com.example.androidproject.data.remote.api.WebServiceAPI;
import com.example.androidproject.data.remote.net.ApiClient;
import java.util.Collections;
import java.util.List;
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
        Context ctx = context.getApplicationContext();
        this.labelApi = ApiClient.getClient(ctx).create(WebServiceAPI.class);
    }

    public void getLabels(Callback<List<Label>> callback) {
        labelApi.getLabels().enqueue(callback);
    }
    public void createLabel(String labelName, Callback<Label> callback) {
        labelApi.createLabel(labelName).enqueue(callback);
    }

    public void updateLabel(String oldName, String newName, Callback<Label> callback) {
        labelApi.updateLabel(oldName, newName).enqueue(callback);
    }

    public void deleteLabel(String labelName, Callback<Void> callback) {
        labelApi.deleteLabel(labelName).enqueue(callback);
    }
}
