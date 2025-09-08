package com.example.androidproject.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.androidproject.data.models.Label;
import com.example.androidproject.data.repository.LabelRepository;

import java.util.List;

import retrofit2.Callback;

public class LabelsViewModel extends ViewModel {
    private final LabelRepository repository;
    private final LiveData<List<Label>> labels;

    public LabelsViewModel() {
        repository = new LabelRepository();
        labels = repository.getLabels();
    }

    public LiveData<List<Label>> getLabels() {
        return repository.getLabels();
    }

    public void refreshLabels() {
        repository.refreshLabels();
    }

    public void createLabel(String labelName, Callback<Label> cb) {
        repository.createLabel(labelName, cb);
    }

    public void deleteLabel(String labelName) {
        repository.deleteLabel(labelName);
    }

    public void updateLabel(String oldName, String newName) {
        repository.updateLabel(oldName, newName);
    }
}
