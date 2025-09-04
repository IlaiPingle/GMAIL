package com.example.androidproject.viewModel;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedMailStateViewModel extends ViewModel {
    private final MutableLiveData<String> selectedLabel = new MutableLiveData<>(null);

    public LiveData<String> getSelectedLabel() {
        return selectedLabel;
    }

    public void setSelectedLabel(@Nullable String label) {
        selectedLabel.setValue(label);
    }
}

