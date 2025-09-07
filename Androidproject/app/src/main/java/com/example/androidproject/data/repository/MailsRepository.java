package com.example.androidproject.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.androidproject.data.local.dao.MailDao;
import com.example.androidproject.data.local.db.AppDB;
import com.example.androidproject.data.local.db.MyApplication;
import com.example.androidproject.data.models.Mail;

import com.example.androidproject.data.remote.net.MailAPIClient;

import java.util.ArrayList;
import java.util.List;

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
    private final MutableLiveData<List<Mail>> mails = new MutableLiveData<>();
    public MailsRepository() {
        AppDB db = AppDB.getInstance(MyApplication.context);
        this.mailDao = db.mailDao();
        this.mailApi = new MailAPIClient();
    }

    public LiveData<List<Mail>> getMails() {
        new Thread(() -> mails.postValue(mailDao.getMails())).start();

        mailApi.getMails(new Callback<List<Mail>>() {
            @Override
            public void onResponse(Call<List<Mail>> call, Response<List<Mail>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    new Thread(() -> {
                        mailDao.clear();
                        mailDao.insertList(response.body());
                        mails.postValue(mailDao.getMails());
                    }).start();
                }
            }

            @Override
            public void onFailure(Call<List<Mail>> call, Throwable t) {
                t.printStackTrace();
            }
        });
        return mails;
    }
    public void createDraft(Mail mail) {
        mailApi.createDraft(mail, new Callback<Mail>() {
            @Override
            public void onResponse(Call<Mail> call, Response<Mail> response) {
                if (response.isSuccessful() && response.body() != null) {
                    new Thread(() -> {
                        mailDao.insert(response.body());
                        mails.postValue(mailDao.getMails());
                    }).start();
                }
            }

            @Override
            public void onFailure(Call<Mail> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void sendMail(Mail mail) {
        mailApi.sendMail(mail.getId(), mail, new Callback<Mail>() {
            @Override
            public void onResponse(Call<Mail> call, Response<Mail> response) {
                if (response.isSuccessful() && response.body() != null) {
                    new Thread(() -> {
                        mailDao.update(response.body());
                        mails.postValue(mailDao.getMails());
                    }).start();
                }
            }

            @Override
            public void onFailure(Call<Mail> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void updateMail(Mail mail) {
        mailApi.updateMail(mail.getId(), mail, new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    new Thread(() -> {
                        mailDao.update(mail);
                        mails.postValue(mailDao.getMails());
                    }).start();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void deleteMail(Mail mail) {
        mailApi.deleteMail(mail.getId(), new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    new Thread(() -> {
                        mailDao.delete(mail);
                        mails.postValue(mailDao.getMails());
                    }).start();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public LiveData<List<Mail>> searchMails(String query) {
        MutableLiveData<List<Mail>> searchResults = new MutableLiveData<>();
        mailApi.searchMails(query, new Callback<List<Mail>>() {
            @Override
            public void onResponse(Call<List<Mail>> call, Response<List<Mail>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    searchResults.postValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Mail>> call, Throwable t) {
                t.printStackTrace();
            }
        });

        return searchResults;
    }

    public LiveData<List<Mail>> getMailsByLabel(String label) {
        MutableLiveData<List<Mail>> filteredMails = new MutableLiveData<>();
        new Thread(() -> {
            List<Mail> allMails = mailDao.getMails();
            List<Mail> result = new ArrayList<>();
            boolean isBin = "bin".equalsIgnoreCase(label);
            for (Mail m : allMails) {
                boolean has = m.getLabels() != null && m.getLabels().contains(label);
                boolean inBin = m.getLabels() != null && m.getLabels().contains("bin");
                if (has && (isBin || !inBin)) {
                    result.add(m);
                }
            }
            filteredMails.postValue(result);
        }).start();
        mailApi.getMails(new Callback<List<Mail>>() {
            @Override
            public void onResponse(Call<List<Mail>> call, Response<List<Mail>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    new Thread(() -> {
                        mailDao.clear();
                        mailDao.insertList(response.body());
                        List<Mail> allMails = mailDao.getMails();
                        List<Mail> result = new ArrayList<>();
                        boolean isBin = "bin".equalsIgnoreCase(label);
                        for (Mail m : allMails) {
                            boolean has = m.getLabels() != null && m.getLabels().contains(label);
                            boolean inBin = m.getLabels() != null && m.getLabels().contains("bin");
                            if (has && (isBin || !inBin)) {
                                result.add(m);
                            }
                        }
                        filteredMails.postValue(result);
                    }).start();
                }
            }
            @Override
            public void onFailure(Call<List<Mail>> call, Throwable t) {
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
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Optionally refresh the mail to get updated labels
                    refreshMail(mailId);
                }
            }

            @Override
            public void onFailure(Call<Void> c, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void removeLabelFromMail(String mailId, String labelName) {
        mailApi.removeLabelFromMail(mailId, labelName, new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Optionally refresh the mail to get updated labels
                    refreshMail(mailId);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public LiveData<Mail> getMailById(String mailId) {
        LiveData<Mail> live = mailDao.getMail(mailId);
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
        return live;
    }

    public void deleteMailById(String mailId) {
        mailApi.deleteMail(mailId, new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    new Thread(() -> {
                        mailDao.deleteById(mailId);
                        mails.postValue(mailDao.getMails());
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

