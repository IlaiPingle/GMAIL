package com.example.androidproject.data.repository;

import android.content.Context;
import android.util.Log;

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

/**
 * Repository class for managing Label data from both local database and remote API.
 * It provides methods to fetch, create, update, and delete labels.
 */
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

    /**
     * Fetches all labels from the local database and refreshes them from the remote API.
     *
     * @return LiveData list of labels.
     */
    public LiveData<List<Label>> getLabels() {
        return labelDao.getAllLabels();
    }

    public void fetchLabelsFromServer() {
        labelApi.getLabels(new retrofit2.Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> resp) {
                int code = resp.code();
                android.util.Log.d("LabelRepo", "code=" + code);
                if (!resp.isSuccessful() || resp.body() == null) {
                    try {
                        android.util.Log.e("LabelRepo", "errorBody=" + (resp.errorBody() == null ? "null" : resp.errorBody().string()));
                    } catch (Exception ignored) {
                    }
                    return; // early return on error
                }
                List<String> res = resp.body();
                List<Label> fresh = new ArrayList<>();
                for (String name : res) {
                    fresh.add(new Label(name));
                }
                new Thread(() -> {
                    labelDao.clear();
                    labelDao.insertAll(fresh);
                }).start();
            }
            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Log.e("LabelRepo", "api failed", t);
            }
        });
    }

    public void createLabel(String labelName) {
        createLabel(labelName, null);
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

    public void deleteLabel(String labelName, Callback<Void> callback) {
        labelApi.deleteLabel(labelName, new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    new Thread(() -> labelDao.deleteByName(labelName)).start();
                }
                if (callback != null) {
                    callback.onResponse(call, response);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (callback != null) {
                    callback.onFailure(call, t);
                }
            }
        });
    }

    public void updateLabel(String oldName, String newName, Callback<Void> callback) {
        labelApi.updateLabel(oldName, newName, new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) {
                    try {
                        Log.e("LabelRepo", "update failed code=" + response.code()
                                + " Body=" + (response.errorBody() == null ? "" : response.errorBody().string()));
                    } catch (Exception ignored) {}
                }
                if (response.isSuccessful()) {
                    new Thread(() -> {
                        labelDao.deleteByName(oldName);
                        labelDao.insert(new Label(newName));
                    }).start();
                    fetchLabelsFromServer();
                }
                if (callback != null) {
                    callback.onResponse(call, response);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (callback != null) {
                    callback.onFailure(call, t);
                }
            }
        });
    }
}
