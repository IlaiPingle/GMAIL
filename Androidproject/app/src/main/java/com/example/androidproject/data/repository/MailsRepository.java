package com.example.androidproject.data.repository;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.androidproject.data.local.dao.MailDao;
import com.example.androidproject.data.local.db.AppDB;
import com.example.androidproject.data.models.Mail;

import com.example.androidproject.data.remote.net.MailAPIClient;
import com.example.androidproject.data.remote.net.Resource;
import com.example.androidproject.util.ApiErrorParser;
import com.example.androidproject.util.UrlUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Callback;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Repository class for managing Mail data from both local database and remote API.
 * This class provides methods to fetch, create, update, delete, and search mails,
 * as well as manage labels associated with mails.
 * It uses Room for local data storage and Retrofit for network operations.
 */
public class MailsRepository {
    private final MailDao mailDao;
    private final MailAPIClient mailApi;
    private final Executor executor = Executors.newSingleThreadExecutor();

    public  MailsRepository(Context context) {
        AppDB db = AppDB.getInstance(context.getApplicationContext());
        this.mailDao = db.mailDao();
        this.mailApi = new MailAPIClient(context);
    }
    public LiveData<Resource<Void>> fetchMailsFromServer() {
        MutableLiveData<Resource<Void>> result = new MutableLiveData<>();
        mailApi.getMails(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<Mail>> call, @NonNull Response<List<Mail>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    executor.execute(() ->
                            mailDao.insertList(response.body()));
                    result.postValue(Resource.success(null));
                } else {
                    result.postValue(Resource.error(ApiErrorParser.parseMessage(response), response.code()));
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<Mail>> call, @NonNull Throwable t) {
                result.postValue(Resource.error("Network error: " + t.getMessage(), -1));
            }
        });
        return result;
    }

    public LiveData<Resource<List<Mail>>> getMailsByLabel(String label) {
        MediatorLiveData<Resource<List<Mail>>> result = new MediatorLiveData<>();
        result.setValue(Resource.loading(null));
        LiveData<List<Mail>> allLocalMails = mailDao.getMails();
        result.addSource(allLocalMails, all -> executor.execute(() -> result.postValue(Resource.success(filterByLabel(all, label)))));
        mailApi.getMailsByLabel(label, new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<Mail>> call, @NonNull Response<List<Mail>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.postValue(Resource.success(response.body()));
                    executor.execute(() -> mailDao.insertList(response.body()));
                } else {
                    result.postValue(Resource.error(ApiErrorParser.parseMessage(response), response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Mail>> call, @NonNull Throwable t) {
                result.postValue(Resource.error("Network error: " + t.getMessage(), -1));
            }
        });
        return result;
    }
    public LiveData<Resource<List<Mail>>> searchMails(String query) {
        MediatorLiveData<Resource<List<Mail>>> searchResults = new MediatorLiveData<>();
        searchResults.setValue(Resource.loading(null));
        LiveData<List<Mail>> allLocalMails = mailDao.getMails();
        searchResults.addSource(allLocalMails, all ->
                executor.execute(() -> searchResults.postValue(Resource.success(filterByQuery(all, query)))));
        mailApi.searchMails(query, new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<Mail>> call, @NonNull Response<List<Mail>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Mail> remoteMails = response.body();
                    searchResults.postValue(Resource.success(remoteMails));
                    executor.execute(() -> mailDao.insertList(remoteMails));
                } else {
                    searchResults.postValue(Resource.error(ApiErrorParser.parseMessage(response), response.code()));
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<Mail>> call, @NonNull Throwable t) {
                searchResults.postValue(Resource.error("Network error: " + t.getMessage(), -1));
            }
        });
        return searchResults;
    }
    public LiveData<Resource<Mail>> getMailById(String mailId) {
        MediatorLiveData<Resource<Mail>> result = new MediatorLiveData<>();
        result.setValue(Resource.loading(null));
        LiveData<Mail> local = mailDao.getMail(mailId); // LiveData from Room
        result.addSource(local, mail -> {
            Resource<Mail> curr = result.getValue();
            if (curr != null && curr.isError()) {
                result.setValue(Resource.error(curr.getMessage(), curr.getErrorCode(), mail, curr.getCause()));
            } else {
                result.setValue(Resource.success(mail));
            }
        });
        mailApi.getMailById(mailId, new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Mail> call, @NonNull Response<Mail> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Mail remoteMail = response.body();
                    executor.execute(() -> mailDao.insert(remoteMail));
                } else if (response.code() == 404) {
                    executor.execute(() -> {
                        Mail current = mailDao.getMailNow(mailId);
                        if (current != null) mailDao.delete(current);
                    });
                    result.postValue(Resource.error(ApiErrorParser.parseMessage(response), response.code(), null, null));
                } else {
                    result.postValue(Resource.error(ApiErrorParser.parseMessage(response), response.code(), local.getValue(), null));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Mail> call, @NonNull Throwable t) {
                result.postValue(Resource.error("Network error: " + t.getMessage(), -1, local.getValue(), t));
            }
        });

        return result;
    }

    public LiveData<Resource<Mail>> createDraft(Mail mail) {
        MutableLiveData<Resource<Mail>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        mailApi.createDraft(mail, new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Mail> call, @NonNull Response<Mail> response) {
                if (response.isSuccessful() && response.body() != null) {
                    executor.execute(() -> mailDao.insert(response.body()));
                    result.postValue(Resource.success(response.body()));
                } else {
                    String message = ApiErrorParser.parseMessage(response);
                    result.postValue(Resource.error(message, response.code()));
                }
            }
            @Override
            public void onFailure(@NonNull Call<Mail> call, @NonNull Throwable t) {
                result.postValue(Resource.error("Network error: " + t.getMessage(), -1, null, t));
            }
        });
        return result;
    }

    public LiveData<Resource<Void>> sendMail(Mail mail) {
        MutableLiveData<Resource<Void>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        mailApi.sendMail(mail.getId(), mail, new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Mail> call, @NonNull Response<Mail> response) {
                if (response.isSuccessful() && response.body() != null) {
                    executor.execute(() -> mailDao.insert(response.body()));
                    result.postValue(Resource.success(null));
                } else if (response.code() == 404) {
                    executor.execute(() -> mailDao.delete(mail));
                    result.postValue(Resource.error(ApiErrorParser.parseMessage(response), 404));
                } else {
                    String message = ApiErrorParser.parseMessage(response);
                    result.postValue(Resource.error(message, response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Mail> call, @NonNull Throwable t) {
                result.postValue(Resource.error("Network error: " + t.getMessage(), -1, null, t));
            }
        });
        return result;
    }

    public LiveData<Resource<Void>> updateMail(Mail mail) {
        MutableLiveData<Resource<Void>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        mailApi.updateMail(mail.getId(), mail, new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    executor.execute(() -> mailDao.update(mail));
                    result.postValue(Resource.success(null));
                } else if (response.code() == 404) {
                    executor.execute(() ->
                            mailDao.delete(mail)
                    );
                    result.postValue(Resource.error(ApiErrorParser.parseMessage(response), 404));
                } else {
                    String message = response.message();
                    result.postValue(Resource.error(message, response.code()));
                }

            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                result.postValue(Resource.error("Network error: " + t.getMessage(), -1, null, t));
            }
        });
        return result;
    }

    public LiveData<Resource<Void>> deleteMail(Mail mail) {
        MutableLiveData<Resource<Void>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        mailApi.deleteMail(mail.getId(), new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    executor.execute(() -> mailDao.delete(mail));
                    result.postValue(Resource.success(null));
                } else if (response.code() == 404) {
                    executor.execute(() -> mailDao.delete(mail));
                    result.postValue(Resource.error(ApiErrorParser.parseMessage(response), 404));
                } else {
                    String message = ApiErrorParser.parseMessage(response);
                    result.postValue(Resource.error(message, response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                result.postValue(Resource.error("Network error: " + t.getMessage(), -1, null, t));
            }
        });
        return result;
    }






    public void refreshMail(String mailId) {
        mailApi.getMailById(mailId, new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Mail> call, @NonNull Response<Mail> response) {
                if (response.isSuccessful() && response.body() != null) {
                    executor.execute(() -> mailDao.insert(response.body()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Mail> call, @NonNull Throwable t) {
                Log.e("MailsRepository", "Failed to refresh mail: " + mailId, t);
            }
        });
    }
    public LiveData<Resource<Void>> addLabelToMail(String mailId, String labelName) {
        MutableLiveData<Resource<Void>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        mailApi.addLabelToMail(mailId, labelName, new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    executor.execute(() -> {
                        Mail mail = mailDao.getMailNow(mailId);
                        if (mail != null) {
                            List<String> labels = mail.getLabels();
                            if (labels == null) {
                                labels = new ArrayList<>();
                            }
                            if (!labels.contains(labelName)) {
                                labels.add(labelName);
                                mail.setLabels(labels);
                                mailDao.update(mail);
                            }
                        }
                    });
                    result.postValue(Resource.success(null));
                } else if (response.code() == 404) {
                    executor.execute(() -> {
                        Mail current = mailDao.getMailNow(mailId);
                        if (current != null) mailDao.delete(current);
                    });
                    result.postValue(Resource.error(ApiErrorParser.parseMessage(response), 404));
                } else {
                    String message = ApiErrorParser.parseMessage(response);
                    result.postValue(Resource.error(message, response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> c, @NonNull Throwable t) {
                result.postValue(Resource.error("Network error: " + t.getMessage(), -1, null, t));
            }
        });
        return result;
    }

    public LiveData<Resource<Void>> removeLabelFromMail(String mailId, String labelName) {
        MutableLiveData<Resource<Void>> result = new MutableLiveData<>();
        result.setValue(Resource.loading(null));
        mailApi.removeLabelFromMail(mailId, labelName, new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    executor.execute(() -> {
                        Mail mail = mailDao.getMailNow(mailId);
                        if (mail != null) {
                            List<String> labels = mail.getLabels();
                            if (labels != null && labels.contains(labelName)) {
                                labels.remove(labelName);
                                mail.setLabels(labels);
                                mailDao.update(mail);
                            }
                        }
                    });
                    result.postValue(Resource.success(null));
                } else if (response.code() == 404) {
                    executor.execute(() -> {
                        Mail current = mailDao.getMailNow(mailId);
                        if (current != null) mailDao.delete(current);
                    });
                    result.postValue(Resource.error(ApiErrorParser.parseMessage(response), 404));
                } else {
                    String message = ApiErrorParser.parseMessage(response);
                    result.postValue(Resource.error(message, response.code(), null, null));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                result.postValue(Resource.error("Network error: " + t.getMessage(), -1, null, t));
            }
        });
        return result;
    }
    private void reportUrls(Mail mail) {
        List<String> urls = UrlUtils.extractUrlsFromMail(mail.getSubject(), mail.getBody(), mail.getSender());
        for (String url : urls) {
            mailApi.addToBlacklist(url, new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    Log.d("MailsRepository", "Reported URL to blacklist: " + url + ", response code: " + response.code());
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    Log.e("MailsRepository", "Failed to report URL to blacklist: " + url, t);
                }
            });
        }
    }

    private void removeUrlsFromBlacklist(Mail mail) {
        List<String> urls = UrlUtils.extractUrlsFromMail(mail.getSubject(), mail.getBody(), mail.getSender());
        for (String url : urls) {
            mailApi.removeFromBlacklist(url, new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    Log.d("MailsRepository", "Removed URL from blacklist: " + url + ", response code: " + response.code());
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    Log.e("MailsRepository", "Failed to remove URL from blacklist: " + url, t);
                }
            });
        }
    }

    public void reportUrlsAsync(Mail mail) {
        if (mail == null) return;
        executor.execute(() -> reportUrls(mail));
    }

    public void removeUrlsFromBlacklistAsync(Mail mail) {
        if (mail == null) return;
        executor.execute(() -> removeUrlsFromBlacklist(mail));
    }

    // Helper methods for filtering and searching mails locally
    private List<Mail> filterByLabel(List<Mail> all, String label) {
        if (all == null) return Collections.emptyList();
        if (label == null || "all".equalsIgnoreCase(label)) return all;
        List<Mail> filtered = new ArrayList<>();
        if (label.equals("spam") || label.equals("bin")) {
            for (Mail mail : all) {
                List<String> labels = mail.getLabels();
                if (labels != null && labels.contains(label)) filtered.add(mail);
            }
            return filtered;
        }
        for (Mail mail : all) {
            List<String> labels = mail.getLabels();
            if (labels != null && labels.contains(label) && !(labels.contains("bin") || labels.contains("spam")))
                filtered.add(mail);
        }
        return filtered;
    }

    private List<Mail> filterByQuery(List<Mail> all, String query) {
        if (all == null) return Collections.emptyList();
        if (query == null || query.trim().isEmpty()) return all;
        String searchQuery = query.trim().toLowerCase();
        List<Mail> res = new ArrayList<>();
        for (Mail mail : all) {
            if (contains(mail.getSender(), searchQuery) ||
                    contains(mail.getSubject(), searchQuery) ||
                    contains(mail.getBody(), searchQuery)) {
                res.add(mail);
            }
        }
        return res;
    }

    private boolean contains(String field, String searchQuery) {
        return field != null && field.toLowerCase().contains(searchQuery);
    }
}