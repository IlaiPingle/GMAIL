package com.example.androidproject.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

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
    private final MutableLiveData<List<Label>> labels = new MutableLiveData<>();

    public LabelRepository() {
        AppDB db = AppDB.getInstance(MyApplication.context);
        this.labelDao = db.labelDao();
        this.labelApi = new LabelAPIClient();
    }

    public LiveData<List<Label>> getLabels() {
        new Thread(() -> labels.postValue(labelDao.getAllLabels().getValue())).start();
        labelApi.getLabels(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Label> freshLabels = new ArrayList<>();
                    for (String name : response.body()) {
                        Label l = new Label();
                        l.setName(name);
                        freshLabels.add(l);
                    }
                    new Thread(() -> {
                        labelDao.clear();
                        labelDao.insertAll(freshLabels);
                        labels.postValue(freshLabels);
                    }).start();
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                t.printStackTrace();
            }
        });

        return labels;
    }

    public void createLabel(String labelName) {
        labelApi.createLabel(labelName, new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Label label = new Label();
                    label.setName(response.body());
                    new Thread(() -> labelDao.insert(label)).start();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                t.printStackTrace();
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
        labelApi.updateLabel(oldName, newName, new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Label label = new Label();
                    label.setName(response.body());
                    new Thread(() -> {
                        labelDao.deleteByName(oldName);
                        labelDao.insert(label);
                    }).start();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
