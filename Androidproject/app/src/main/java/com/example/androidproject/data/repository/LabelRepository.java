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

/**
 * Repository class for managing Label data from both local database and remote API.
 * It provides methods to fetch, create, update, and delete labels.
 */
public class LabelRepository {
    private final LabelDao labelDao;
    private final LabelAPIClient labelApi;

    public LabelRepository() {
        AppDB db = AppDB.getInstance(MyApplication.context);
        this.labelDao = db.labelDao();
        this.labelApi = new LabelAPIClient();
    }

    /**
     * Fetches all labels from the local database and refreshes them from the remote API.
     * @return LiveData list of labels.
     */
    public LiveData<List<Label>> getLabels() {
        refreshLabels();
        return labelDao.getAllLabels();
    }

    /**
     * Refreshes the labels by fetching them from the remote API and updating the local database.
     * This method runs the database operations in a separate thread to avoid blocking the main thread.
     * The fetched labels are inserted into the local database after clearing the existing ones.
     */
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

    /**
     * Creates a new label with the given name using the remote API and inserts it into the local database upon success.
     * @param labelName The name of the label to be created.
     * @param callback A Retrofit callback to handle the API response.
     */
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

    public void deleteLabel(String labelName, Callback<Void> cb) {
        labelApi.deleteLabel(labelName, new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    new Thread(() -> labelDao.deleteByName(labelName)).start();
                }
                if (cb != null) {
                    cb.onResponse(call, response);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (cb != null) {
                    cb.onFailure(call, t);
                }
            }
        });
    }

    public void updateLabel(String oldName, String newName, Callback<Void> cb) {
        labelApi.updateLabel(oldName, newName, new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    new Thread(() -> {
                        labelDao.deleteByName(oldName);
                        labelDao.insert(new Label(newName));
                    }).start();
                }
                if (cb != null) {
                    cb.onResponse(call, response);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (cb != null) {
                    cb.onFailure(call, t);
                }
            }
        });
    }
}
