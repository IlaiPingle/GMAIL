package com.example.androidproject.data.remote.net;

import com.example.androidproject.data.models.Label;
import com.example.androidproject.data.remote.api.WebServiceAPI;
import com.example.androidproject.data.remote.net.ApiClient;

import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LabelAPIClient {
    private final WebServiceAPI labelApi;

    public LabelAPIClient() {
        labelApi = ApiClient.getClient().create(WebServiceAPI.class);
    }

    public void getLabels(Callback<List<String>> callback) {
        labelApi.getLabels().enqueue(callback);
    }

    public void getLabelByName(String labelName, Callback<String> callback) {
        labelApi.getLabelByName(labelName).enqueue(callback);
    }

    public void createLabel(String labelName, Callback<String> callback) {
        labelApi.createLabel(labelName).enqueue(callback);
    }

    public void updateLabel(String oldName, String newName, Callback<String> callback) {
        labelApi.updateLabel(oldName, newName).enqueue(callback);
    }

    public void deleteLabel(String labelName, Callback<Void> callback) {
        labelApi.deleteLabel(labelName).enqueue(callback);
    }
}
