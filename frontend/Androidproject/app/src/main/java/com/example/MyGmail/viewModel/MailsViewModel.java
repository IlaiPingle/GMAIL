package com.example.MyGmail.viewModel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.MyGmail.data.models.Mail;
import com.example.MyGmail.data.remote.net.Resource;
import com.example.MyGmail.data.repository.MailsRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * ViewModel class for managing Mail data and operations.
 * This class interacts with the MailsRepository to supply data to the UI and handle user actions.
 */

public class MailsViewModel extends AndroidViewModel {
    private final MailsRepository repository;
    private final MediatorLiveData<List<Mail>> mailsData = new MediatorLiveData<>();
    private final MediatorLiveData<Resource<List<Mail>>> mailsState = new MediatorLiveData<>();

    private String selectedLabel = "inbox";
    private String currentQuery = null;
    private LiveData<Resource<List<Mail>>> currentMailsSource = null;

    public MailsViewModel(Application application) {
        super(application);
        repository = new MailsRepository(application);
        attachListSource(repository.getMailsByLabel(selectedLabel));
    }

    public LiveData<List<Mail>> observeMailList() {
        return mailsData;
    }

    public LiveData<Resource<List<Mail>>> observeMailsState() {
        return mailsState;
    }

    public void setSelectedLabel(String label) {
        if (label == null || label.trim().isEmpty()) label = "inbox";
        selectedLabel = label;
        currentQuery = null;
        attachListSource(repository.getMailsByLabel(label));
    }

    public void searchMails(String query) {
        currentQuery = (query != null && !query.trim().isEmpty()) ? query : null;
        if (currentQuery == null) {
            attachListSource(repository.getMailsByLabel(selectedLabel));
        } else {
            attachListSource(repository.searchMails(currentQuery));
        }
    }

    public LiveData<Resource<Void>> fetchAllMails() {
        return repository.fetchMailsFromServer();
    }

    private void attachListSource(LiveData<Resource<List<Mail>>> source) {
        if (currentMailsSource != null) {
            mailsData.removeSource(currentMailsSource);
            mailsState.removeSource(currentMailsSource);
        }
        currentMailsSource = source;
        mailsState.addSource(source, resource -> {
            if (resource == null) return;
            switch (resource.getStatus()) {
                case LOADING:
                    mailsState.setValue(Resource.loading(null));
                    break;
                case SUCCESS:
                    mailsState.setValue(Resource.success(resource.getData()));
                    break;
                case ERROR:
                    mailsState.setValue(Resource.error(resource.getMessage(), resource.getErrorCode(), resource.getData(), resource.getCause()));
                    break;
            }
        });
        mailsData.addSource(source, resource -> {
            if (resource == null) return;
            if (resource.getData() != null) {
                mailsData.setValue(resource.getData());
            }
        });
    }

    public void refreshCurrentList() {
        if (currentQuery != null) {
            attachListSource(repository.searchMails(currentQuery));
        } else {
            attachListSource(repository.getMailsByLabel(selectedLabel));
        }
    }

    //  single mail operations
    public LiveData<Resource<Mail>> getMail(String mailId) {
        return repository.getMailById(mailId);
    }

    public void refreshMail(String mailId) {
        repository.refreshMail(mailId);
    }

    public LiveData<Resource<Mail>> createDraft(Mail mail) {
        return repository.createDraft(mail);
    }

    public LiveData<Resource<Void>> sendMail(Mail mail) {
        return repository.sendMail(mail);
    }

    public LiveData<Resource<Void>> deleteMail(Mail mail) {
        return repository.deleteMail(mail);
    }

    public LiveData<Resource<Void>> updateMail(Mail mail) {
        return repository.updateMail(mail);
    }

    public LiveData<Resource<Void>> removeLabelFromMail(Mail mail, String label) {
        if (mail == null) {
            MutableLiveData<Resource<Void>> bad = new MutableLiveData<>();
            bad.setValue(Resource.error("Invalid mail", -1));
            return bad;
        }
        mail.getLabels().remove(label);
        return repository.removeLabelFromMail(mail.getId(), label);
    }

    public LiveData<Resource<Void>> addLabelToMail(Mail mail, String label) {
        if (mail != null) {
            if (mail.getLabels() == null) mail.setLabels(new ArrayList<>());
            if (!mail.getLabels().contains(label)) {
                mail.getLabels().add(label);
                List<Mail> curr = mailsData.getValue();
                if (curr != null) mailsData.setValue(new ArrayList<>(curr));
            }
        }
        return repository.addLabelToMail(mail.getId(), label);
    }


    public LiveData<Resource<Void>> markMailAsSpam(Mail mail) {
        if (mail == null) {
            MutableLiveData<Resource<Void>> bad = new MutableLiveData<>();
            bad.setValue(Resource.error("Invalid mail", -1));
            return bad;
        }
        repository.reportUrlsAsync(mail);

        LiveData<Resource<Void>> res = repository.addLabelToMail(mail.getId(), "spam");

        refreshMail(mail.getId());

        return res;
    }


    public LiveData<Resource<Void>> removeMailFromSpam(Mail mail) {
        if (mail == null) {
            MutableLiveData<Resource<Void>> bad = new MutableLiveData<>();
            bad.setValue(Resource.error("Invalid mail", -1));
            return bad;
        }
        repository.removeUrlsFromBlacklistAsync(mail);

        LiveData<Resource<Void>> res = repository.removeLabelFromMail(mail.getId(), "spam");
        refreshMail(mail.getId());
        return res;
    }

    public LiveData<Resource<Void>> toggleStar(String mailId, boolean isStarred) {
        LiveData<Resource<Void>> result;
        if (isStarred) {
            result = repository.removeLabelFromMail(mailId, "starred");
        } else {
            result = repository.addLabelToMail(mailId, "starred");
        }
        refreshMail(mailId);
        return result;
    }
}
