package com.example.androidproject.data.repository;

import androidx.lifecycle.LiveData;
import com.example.androidproject.data.local.dao.LabelDao;
import com.example.androidproject.data.local.db.AppDB;
import com.example.androidproject.data.local.db.MyApplication;
import com.example.androidproject.data.models.Label;
import com.example.androidproject.data.remote.net.LabelAPIClient;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LabelRepository {
    private final LabelDao labelDao;
    private final LabelAPIClient labelApi;

    public LabelRepository() {
        AppDB db = AppDB.getInstance(MyApplication.context);
        this.labelDao = db.labelDao();
        this.labelApi = new LabelAPIClient();
    }

    public LiveData<List<Label>> getLabels() {
        refreshLabels();
        return labelDao.getAllLabels();
    }

    public void refreshLabels() {
        labelApi.getLabels(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    return;
                }
                List<Label> fresh = new ArrayList<>();
                for (String name : response.body()) {
                    fresh.add(new Label(name));
                }
                new Thread(() -> {
                    labelDao.clear();
                    labelDao.insertAll(fresh);
                }).start();
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void createLabel(String labelName, Callback<Label> callback) {
        labelApi.createLabel(labelName, new Callback<Label>() {
            @Override
            public void onResponse(Call<Label> call, Response<Label> response) {
                if (response.isSuccessful() && response.body() != null) {
                    new Thread(() -> labelDao.insert(response.body())).start();
                }
                if (callback != null) {
                    callback.onResponse(call, response);
                }
            }

            @Override
            public void onFailure(Call<Label> call, Throwable t) {
                if (callback != null) {
                    callback.onFailure(call, t);
                }
            }
        });
    }

    public void deleteLabel(String labelName) {
        labelApi.deleteLabel(labelName, new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    new Thread(() -> labelDao.deleteByName(labelName)).start();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void updateLabel(String oldName, String newName) {
        labelApi.updateLabel(oldName, newName, new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    new Thread(() -> {
                        labelDao.deleteByName(oldName);
                        labelDao.insert(new Label(newName));
                    }).start();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
