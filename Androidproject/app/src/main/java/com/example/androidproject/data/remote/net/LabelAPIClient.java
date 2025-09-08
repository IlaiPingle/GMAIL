package com.example.androidproject.data.remote.net;

import com.example.androidproject.data.models.Label;
import com.example.androidproject.data.remote.api.WebServiceAPI;
import com.example.androidproject.data.remote.net.ApiClient;
import java.util.List;
import java.util.Map;
import retrofit2.Callback;

public class LabelAPIClient {
    private final WebServiceAPI labelApi;

    public LabelAPIClient() {
        labelApi = ApiClient.getClient().create(WebServiceAPI.class);
    }

    public void getLabels(Callback<List<String>> callback) {
        labelApi.getLabels().enqueue(callback);
    }

    public void getLabelByName(String labelName, Callback<Label> callback) {
        labelApi.getLabelByName(labelName).enqueue(callback);
    }

    public void createLabel(String labelName, Callback<Label> callback) {
        labelApi.createLabel(Map.of("labelName", labelName)).enqueue(callback);
    }

    public void updateLabel(String oldName, String newName, Callback<Void> callback) {
        labelApi.updateLabel(oldName, Map.of("newName", newName)).enqueue(callback);
    }

    public void deleteLabel(String labelName, Callback<Void> callback) {
        labelApi.deleteLabel(labelName).enqueue(callback);
    }
}
