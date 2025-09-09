package com.example.androidproject.viewModel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.androidproject.data.models.Mail;
import com.example.androidproject.data.repository.MailsRepository;

import java.util.List;
/**
 * ViewModel class for managing Mail data and operations.
 * This class interacts with the MailsRepository to supply data to the UI and handle user actions.
 */

public class MailsViewModel extends AndroidViewModel {
    private final MailsRepository repository;
    private final LiveData<List<Mail>> mails;
    private String selectedLabel;

    public MailsViewModel(Application application) {
        super(application);
        repository = new MailsRepository(application);
        mails = repository.getMails();
    }

    public LiveData<List<Mail>> getMails() {
        selectedLabel = null;
        return mails;
    }

    public LiveData<Mail> getMailById(String mailId) {
        return repository.getMailById(mailId);
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

    public LiveData<List<Mail>> searchMails(String query) {
        return repository.searchMails(query);
    }

    public LiveData<List<Mail>> getMailsByLabel(String label) {
        selectedLabel = label;
        return repository.getMailsByLabel(label);
    }

    public LiveData<List<Mail>> removeLabelFromMail(Mail mail, String label) {
        repository.removeLabelFromMail(mail.getId(), label);
        return refreshCurrentList();
    }

    public LiveData<List<Mail>> addLabelToMail(Mail mail, String label) {
        repository.addLabelToMail(mail.getId(), label);
        return refreshCurrentList();
    }

    public LiveData<List<Mail>> refreshCurrentList() {
        if (selectedLabel == null ) {
            return repository.getMails();
        } else {
            return repository.getMailsByLabel(selectedLabel);
        }
    }
}
