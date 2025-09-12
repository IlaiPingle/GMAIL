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

    // Factory method to create a new instance of the fragment with the old label name as an argument
    public static EditLabelBottomSheet newInstance(String oldName) {
        Bundle b = new Bundle();
        b.putString(ARG_OLD_NAME, oldName);
        EditLabelBottomSheet f = new EditLabelBottomSheet();
        f.setArguments(b);
        return f;
    }

    private LabelsViewModel labelsViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottomsheet_edit_label, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        String oldName = getArguments() == null ? "" : getArguments().getString(ARG_OLD_NAME, "");

        labelsViewModel = new ViewModelProvider(requireActivity()).get(LabelsViewModel.class);

        TextInputLayout til = v.findViewById(R.id.tilLabelName);
        TextInputEditText et = v.findViewById(R.id.etLabelName);
        MaterialButton btnSave = v.findViewById(R.id.btnSave);
        MaterialButton btnDelete = v.findViewById(R.id.btnDelete);
        MaterialButton btnCancel = v.findViewById(R.id.btnCancel);

        et.setText(oldName);

        final Set<String> existing = new HashSet<>();
        labelsViewModel.getLabels().observe(getViewLifecycleOwner(), list -> {
            existing.clear();
            if (list != null) for (Label l : list) existing.add(l.getName().toLowerCase());
        });

        final Set<String> system = new HashSet<>(Arrays.asList(
                "inbox", "sent", "starred", "snoozed", "spam", "bin", "trash", "drafts", "social", "promotions", "primary"
        ));

        btnSave.setOnClickListener(v1 -> {
            String newName = et.getText() == null ? "" : et.getText().toString().trim();
            til.setError(null);

            if (TextUtils.isEmpty(newName)) {
                til.setError(getString(R.string.error_required));
                return;
            }
            if (newName.length() > 30) {
                til.setError(getString(R.string.error_too_long));
                return;
            }
            String key = newName.toLowerCase();
            if (system.contains(key)) {
                til.setError(getString(R.string.error_system_label));
                return;
            }
            if (!newName.equalsIgnoreCase(oldName) && existing.contains(key)) {
                til.setError(getString(R.string.error_label_exists));
                return;
            }

            btnSave.setEnabled(false);
            labelsViewModel.updateLabel(oldName, newName);
        });

        btnDelete.setOnClickListener(v12 -> {
            btnDelete.setEnabled(false);
            labelsViewModel.deleteLabel(oldName);

            btnCancel.setOnClickListener(v13 -> dismiss());
        });
    }
}