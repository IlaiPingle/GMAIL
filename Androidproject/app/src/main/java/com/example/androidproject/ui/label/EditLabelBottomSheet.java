package com.example.androidproject.ui.label;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.androidproject.R;
import com.example.androidproject.data.models.Label;
import com.example.androidproject.viewModel.LabelsViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A BottomSheetDialogFragment for editing or deleting a label.
 * It allows users to change the name of an existing label or delete it entirely.
 * The fragment interacts with the LabelsViewModel to perform these operations.
 */
public class EditLabelBottomSheet extends BottomSheetDialogFragment {
    private static final String ARG_OLD_NAME = "oldName"; // Argument key for the old label name
    private String originalLabelName;

    // Factory method to create a new instance of the fragment with the old label name as an argument
    public static EditLabelBottomSheet newInstance(String oldName) {
        Bundle b = new Bundle();
        b.putString(ARG_OLD_NAME, oldName);
        EditLabelBottomSheet f = new EditLabelBottomSheet();
        f.setArguments(b);
        return f;
    }

    private LabelsViewModel labelsViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            originalLabelName = getArguments().getString(ARG_OLD_NAME);
            android.util.Log.d("EditLabelBottomSheet", "Editing label: " + originalLabelName);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottomsheet_edit_label, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        labelsViewModel= new ViewModelProvider(requireActivity()).get(LabelsViewModel.class);

        TextInputLayout til = v.findViewById(R.id.tilLabelName);
        TextInputEditText et = v.findViewById(R.id.etLabelName);
        MaterialButton btnSave = v.findViewById(R.id.btnSave);
        MaterialButton btnDelete = v.findViewById(R.id.btnDelete);
        MaterialButton btnCancel = v.findViewById(R.id.btnCancel);

        et.setText(originalLabelName == null ? "" : originalLabelName);

        final Set<String> existing = new HashSet<>();
        labelsViewModel.getLabels().observe(getViewLifecycleOwner(), list -> {
            existing.clear();
            if (list != null) for (Label l : list) existing.add(l.getName().toLowerCase());
        });

        final Set<String> system = new HashSet<>(Arrays.asList(
                "inbox", "starred", "snoozed", "important", "chats", "sent", "drafts", "bin", "spam", "all", "scheduled", "unread"
        ));

        btnSave.setOnClickListener(v1 -> {
            String oldName = originalLabelName;
            String newName = et.getText() == null ? "" : et.getText().toString().trim();
            til.setError(null);

            if (TextUtils.isEmpty(oldName)) {
                til.setError("Invalid original label name");
                return;
            }

            if (TextUtils.isEmpty(newName)) {
                til.setError(getString(R.string.error_required));
                return;
            }
            if (newName.length() > 30) {
                til.setError(getString(R.string.error_too_long));
                return;
            }
            if (newName.equalsIgnoreCase(oldName)) {
                dismiss();
                return;
            }
            if (system.contains(newName.toLowerCase())) {
                til.setError(getString(R.string.error_system_label));
                return;
            }

            btnSave.setEnabled(false);
            labelsViewModel.updateLabel(oldName, newName, new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    btnSave.setEnabled(true);
                    if (response.isSuccessful()) {
                        dismiss();
                    } else {
                        til.setError(response.code() == 409 ? getString(R.string.error_label_exists)
                                : response.code() == 404 ? "Label not found"
                                : response.code() == 400 ? getString(R.string.error_system_label)
                                : "Update failed: (" + response.code() + ")");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    btnSave.setEnabled(true);
                    til.setError(t.getMessage() == null ? "Network error" : t.getMessage());
                }
            });
        });

        btnDelete.setOnClickListener(v12 -> {
            String name = originalLabelName;
            til.setError(null);
            if (TextUtils.isEmpty(name)) {
                til.setError("Invalid label name");
                return;
            }
            btnDelete.setEnabled(false);
            labelsViewModel.deleteLabel(originalLabelName, new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    btnDelete.setEnabled(true);
                    if (response.isSuccessful()) {
                        dismiss();
                    } else {
                        til.setError(response.code() == 404 ? "Label not found"
                                : response.code() == 400 ? "Cannot delete system label"
                                : "Failed to delete label: (" + response.code() + ")");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    btnDelete.setEnabled(true);
                    til.setError(t.getMessage() == null ? "Network error" : t.getMessage());
                }
            });
        });

        btnCancel.setOnClickListener(v13 -> dismiss());
    }
}