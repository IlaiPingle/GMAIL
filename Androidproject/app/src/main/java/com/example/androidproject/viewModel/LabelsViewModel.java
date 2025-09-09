package com.example.androidproject.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.androidproject.data.models.Label;
import com.example.androidproject.data.repository.LabelRepository;

import java.util.List;

public class LabelsViewModel extends AndroidViewModel {

    private final LabelRepository repository;
    private final LiveData<List<Label>> labels;

    public LabelsViewModel(@NonNull Application app) {
        super(app);
        repository = new LabelRepository(app);
        labels = repository.getLabels();
        repository.fetchLabelsFromServer();
    }

    public LiveData<List<Label>> getLabels() {
        return labels;
    }

    public void refreshLabels() {
        repository.fetchLabelsFromServer();
    }

    public void createLabel(String labelName) {
        repository.createLabel(labelName);
    }

    public void deleteLabel(String labelName) {
        repository.deleteLabel(labelName);
    }

    public void updateLabel(String oldName, String newName) {
        repository.updateLabel(oldName, newName);
    }
}