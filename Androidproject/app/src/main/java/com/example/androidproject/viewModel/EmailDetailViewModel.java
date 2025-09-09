package com.example.androidproject.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.androidproject.data.models.Mail;
import com.example.androidproject.data.repository.MailsRepository;

public class EmailDetailViewModel extends ViewModel {
    private final MailsRepository repo = new MailsRepository();

    public LiveData<Mail> getMail(String id) {
        return repo.getMailById(id);
    }
    public void refreshMail(String id) {
        repo.refreshMail(id);
    }
    public void toggleStar(String id, boolean isStarred) {
        repo.toggleStar(id, isStarred);
    }
    public void deleteMailById(String id) {
        repo.deleteMailById(id);
    }

    public void deleteMail(Mail mail) {
        repo.deleteMail(mail);
    }

    public void addLabelToMail(Mail mail, String label) {
        repo.addLabelToMail(mail.getId(), label);
    }

    public void removeLabelFromMail(Mail mail, String label) {
        repo.removeLabelFromMail(mail.getId(), label);
    }
}