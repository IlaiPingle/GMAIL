package com.example.androidproject.data.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.androidproject.data.local.dao.LabelDao;
import com.example.androidproject.data.local.db.AppDB;
import com.example.androidproject.data.local.db.MyApplication;
import com.example.androidproject.data.models.Label;
import com.example.androidproject.data.remote.net.LabelAPIClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LabelRepository {
    private final LabelDao labelDao;
    private final LabelAPIClient labelApi;

    public LabelRepository(Context context) {
        Context ctx = context.getApplicationContext();
        AppDB db = AppDB.getInstance(ctx);
        this.labelDao = db.labelDao();
        this.labelApi = new LabelAPIClient(ctx);
        fetchLabelsFromServer();
    }

    public LiveData<List<Label>> getLabels() {
        return labelDao.getAllLabels();
    }

    public void fetchLabelsFromServer() {
        labelApi.getLabels(new retrofit2.Callback<List<Label>>() {
            @Override
            public void onResponse(Call<List<Label>> call, Response<List<Label>> resp) {
                int code = resp.code();
                android.util.Log.d("LabelRepo", "code=" + code);
                if (!resp.isSuccessful() || resp.body() == null) {
                    try {
                        android.util.Log.e("LabelRepo", "errorBody=" + (resp.errorBody() == null ? "null" : resp.errorBody().string()));
                    } catch (Exception ignored) {
                    }
                    return; // early return on error
                }
                List<Label> fresh = resp.body();
                new Thread(() -> {
                    labelDao.clear();
                    labelDao.insertAll(fresh);
                }).start();
            }

            @Override
            public void onFailure(Call<List<Label>> call, Throwable t) {
                Log.e("LabelRepo", "api failed", t);
            }
        });
    }

    public void createLabel(String labelName) {
        labelApi.createLabel(labelName, new Callback<Label>() {
            @Override
            public void onResponse(Call<Label> call, Response<Label> response) {
                if (response.isSuccessful() && response.body() != null) {
                    new Thread(() -> labelDao.insert(response.body())).start();
                }
            }

            @Override
            public void onFailure(Call<Label> call, Throwable t) {
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
        labelApi.updateLabel(oldName, newName, new Callback<Label>() {
            @Override
            public void onResponse(Call<Label> call, Response<Label> response) {
                if (response.isSuccessful() && response.body() != null) {
                    new Thread(() -> {
                        labelDao.deleteByName(oldName);
                        labelDao.insert(response.body());
                    }).start();
                }
            }

            @Override
            public void onFailure(Call<Label> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
