package com.example.androidproject.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.androidproject.data.models.Label;
import com.example.androidproject.data.repository.LabelRepository;

import java.util.List;

import retrofit2.Callback;

/**
 * ViewModel class for managing Label data.
 * It interacts with the LabelRepository to fetch, create, update, and delete labels.
 * The ViewModel provides LiveData for observing label data changes.
 */
public class LabelsViewModel extends ViewModel {
    private final LabelRepository repository;
    private final LiveData<List<Label>> labels;

    public LabelsViewModel(@NonNull Application app) {
        super(app);
        repository = new LabelRepository(app);
        labels = repository.getLabels();
        repository.fetchLabelsFromServer();
    }

    /**
     * Gets the LiveData list of labels.
     * @return LiveData list of labels.
     */
    public LiveData<List<Label>> getLabels() {
        return repository.getLabels();
    }

    public void refreshLabels() {
        repository.fetchLabelsFromServer();
    }

    public void createLabel(String labelName) {
        repository.createLabel(labelName);
    }

    /**
     * Creates a new label with the given name.
     * The result of the operation is provided via the Callback parameter.
     * @param labelName The name of the label to be created.
     * @param cb Callback to handle the response or failure of the create operation.
     */
    public void createLabel(String labelName, Callback<Label> cb) {
        repository.createLabel(labelName, cb);
    }

    /**
     * Deletes the label with the given name.
     * The result of the operation is provided via the Callback parameter.
     * @param labelName The name of the label to be deleted.
     * @param cb Callback to handle the response or failure of the delete operation.
     */
    public void deleteLabel(String labelName, Callback<Void> cb) {
        repository.deleteLabel(labelName, cb);
    }

    /**
     * Updates the name of an existing label.
     * The result of the operation is provided via the Callback parameter.
     * @param oldName The current name of the label to be updated.
     * @param newName The new name for the label.
     * @param cb Callback to handle the response or failure of the update operation.
     */
    public void updateLabel(String oldName, String newName, Callback<Void> cb) {
        repository.updateLabel(oldName, newName, cb);
    }
}