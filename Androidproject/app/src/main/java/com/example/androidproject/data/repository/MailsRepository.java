package com.example.androidproject.data.repository;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.example.androidproject.data.local.dao.MailDao;
import com.example.androidproject.data.local.db.AppDB;
import com.example.androidproject.data.models.Mail;

import com.example.androidproject.data.remote.net.MailAPIClient;

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

    public MailsRepository(Context context) {
        AppDB db = AppDB.getInstance(context.getApplicationContext());
        this.mailDao = db.mailDao();
        this.mailApi = new MailAPIClient(context);
    }

    public LiveData<List<Mail>> getMails() {
        LiveData<List<Mail>> localMails = mailDao.getMails();
        mailApi.getMails(new Callback<List<Mail>>() {
            @Override
            public void onResponse(@NonNull Call<List<Mail>> call, @NonNull Response<List<Mail>> response) {
                if (response.isSuccessful() && response.body() != null) executor.execute(() -> {
                    mailDao.insertList(response.body());
                });
            }

            @Override
            public void onFailure(@NonNull Call<List<Mail>> call, @NonNull Throwable t) {

            }
        });
        return localMails;
    }
    public LiveData<Mail> getMailById(String mailId) {
        LiveData<Mail> local = mailDao.getMail(mailId); // LiveData from Room

        mailApi.getMailById(mailId, new Callback<Mail>() {
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
                }
            }
            @Override
            public void onFailure(@NonNull Call<Mail> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });

        return local;
    }

    public void createDraft(Mail mail) {
        mailApi.createDraft(mail, new Callback<Mail>() {
            @Override
            public void onResponse(@NonNull Call<Mail> call, @NonNull Response<Mail> response) {
                if (response.isSuccessful() && response.body() != null)
                    executor.execute(() -> mailDao.insert(response.body()));
            }

            @Override
            public void onFailure(@NonNull Call<Mail> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void sendMail(Mail mail) {
        mailApi.sendMail(mail.getId(), mail, new Callback<Mail>() {
            @Override
            public void onResponse(@NonNull Call<Mail> call, @NonNull Response<Mail> response) {
                if (response.isSuccessful() && response.body() != null)
                    executor.execute(() -> mailDao.insert(response.body()));
            }

            @Override
            public void onFailure(@NonNull Call<Mail> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void updateMail(Mail mail) {
        mailApi.updateMail(mail.getId(), mail, new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    executor.execute(() -> mailDao.update(mail));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void deleteMail(Mail mail) {
        mailApi.deleteMail(mail.getId(), new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful())
                    executor.execute(() -> mailDao.delete(mail));
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public LiveData<List<Mail>> searchMails(String query) {
        MediatorLiveData<List<Mail>> searchResults = new MediatorLiveData<>();
        LiveData<List<Mail>> allLocalMails = mailDao.getMails();
        searchResults.addSource(allLocalMails, all -> {
            executor.execute(() -> searchResults.postValue(filterByQuery(all, query)));
        });

        mailApi.searchMails(query, new Callback<List<Mail>>() {
            @Override
            public void onResponse(@NonNull Call<List<Mail>> call, @NonNull Response<List<Mail>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Mail> remoteMails = response.body();
                    searchResults.postValue(remoteMails);
                    executor.execute(() -> mailDao.insertList(remoteMails));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Mail>> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });
        return searchResults;
    }

    public LiveData<List<Mail>> getMailsByLabel(String label) {
        MediatorLiveData<List<Mail>> filteredMails = new MediatorLiveData<>();
        LiveData<List<Mail>> allLocalMails = mailDao.getMails();
        filteredMails.addSource(allLocalMails, all -> {
            executor.execute(() -> filteredMails.postValue(filterByLabel(all, label)));
        });
        mailApi.getMailsByLabel(label, new Callback<List<Mail>>() {
            @Override
            public void onResponse(@NonNull Call<List<Mail>> call, @NonNull Response<List<Mail>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    filteredMails.postValue(response.body());
                    executor.execute(() -> mailDao.insertList(response.body()));
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<Mail>> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });
        return filteredMails;
    }

    public void refreshMail(String mailId) {
        mailApi.getMailById(mailId, new Callback<Mail>() {
            @Override
            public void onResponse(Call<Mail> call, Response<Mail> response) {
                if (response.isSuccessful() && response.body() != null) {
                    new Thread(() -> mailDao.insert(response.body())).start();
                }
            }
            @Override
            public void onFailure(Call<Mail> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void toggleStar(String mailId, boolean isStarred) {
        if (isStarred) {
            addLabelToMail(mailId, "starred");
        } else {
            removeLabelFromMail(mailId, "starred");
        }
        refreshMail(mailId);
    }

    public void addLabelToMail(String mailId, String labelName) {
        mailApi.addLabelToMail(mailId, labelName, new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if(response.isSuccessful()) {
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
                }
            }
            @Override
            public void onFailure(@NonNull Call<Void> c, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void removeLabelFromMail(String mailId, String labelName) {
        mailApi.removeLabelFromMail(mailId, labelName, new Callback<Void>() {
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
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });
    }

    // Helper methods for filtering and searching mails locally
    private List<Mail> filterByLabel(List<Mail> all, String label) {
        if (all == null) return Collections.emptyList();
        if (label == null || "all".equalsIgnoreCase(label)) return all;
        List<Mail> filtered = new ArrayList<>();
        for (Mail mail : all) {
            List<String> labels = mail.getLabels();
            if (labels != null && labels.contains(label)) filtered.add(mail);
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