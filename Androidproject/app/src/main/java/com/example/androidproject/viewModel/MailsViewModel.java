package com.example.androidproject.viewModel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.example.androidproject.data.models.Mail;
import com.example.androidproject.data.repository.MailsRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * ViewModel class for managing Mail data and operations.
 * This class interacts with the MailsRepository to supply data to the UI and handle user actions.
 */

public class MailsViewModel extends AndroidViewModel {
    private final MailsRepository repository;
    private final MediatorLiveData<List<Mail>> mailsData = new MediatorLiveData<>();
    private final LiveData<List<Mail>> mails = mailsData;

    private String selectedLabel = "all";
    private LiveData<List<Mail>> currentMailsSource = null;

    public MailsViewModel(Application application) {
        super(application);
        repository = new MailsRepository(application);
        loadMails();
    }

    public LiveData<List<Mail>> observeMailList() {
        return mails;
    }

    public void loadMails() {
        LiveData<List<Mail>> mailsSource =
                (selectedLabel == null || "all".equals(selectedLabel))
                        ? repository.getMails()
                        : repository.getMailsByLabel(selectedLabel);
        if (currentMailsSource != null) {
            mailsData.removeSource(currentMailsSource);
        }
        currentMailsSource = mailsSource;
        mailsData.addSource(currentMailsSource, mailsData::setValue);
    }


    public LiveData<Mail> getMail(String mailId) {
        return repository.getMailById(mailId);
    }
    public void refreshMail(String mailId) {
        repository.refreshMail(mailId);
    }
    public void toggleStar(String mailId, boolean isStarred) {
        repository.toggleStar(mailId, isStarred);
    }
    public void createDraft(Mail mail) {
        repository.createDraft(mail);
    }

    public void deleteMail(Mail mail) {
        repository.deleteMail(mail);
    }

    public void updateMail(Mail mail) {
        repository.updateMail(mail);
    }

    public void sendMail(Mail mail) {
        repository.sendMail(mail);
    }

    public void searchMails(String query) {
        if (query == null || query.trim().isEmpty()) {
            loadMails();
            return;
        }
        LiveData<List<Mail>> searchResults = repository.searchMails(query);
        if (currentMailsSource != null) {
            mailsData.removeSource(currentMailsSource);
        }
        currentMailsSource = searchResults;
        mailsData.addSource(currentMailsSource, mailsData::setValue);
    }

    public void getMailsByLabel(String label) {
        selectedLabel = label;
        loadMails();
    }

    public void removeLabelFromMail(Mail mail, String label) {
        mail.getLabels().remove(label);
        repository.removeLabelFromMail(mail.getId(), label);
        loadMails();
    }

    public void addLabelToMail(Mail mail, String label) {
        if (mail != null) {
            if (mail.getLabels() == null) mail.setLabels(new ArrayList<>());
            if (!mail.getLabels().contains(label)) {
                mail.getLabels().add(label);
                List<Mail> curr = mailsData.getValue();
                if (curr != null) mailsData.setValue(new ArrayList<>(curr));
            }
        }
        repository.addLabelToMail(mail.getId(), label);
    }
}
