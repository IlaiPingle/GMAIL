package com.example.androidproject.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

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

    public LabelsViewModel() {
        repository = new LabelRepository();
        labels = repository.getLabels();
    }

    /**
     * Gets the LiveData list of labels.
     * @return LiveData list of labels.
     */
    public LiveData<List<Label>> getLabels() {
        return repository.getLabels();
    }

    /**
     * Refreshes the labels by fetching them from the remote API and updating the local database.
     * This method can be called to manually trigger a refresh of the label data.
     */
    public void refreshLabels() {
        repository.refreshLabels();
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
