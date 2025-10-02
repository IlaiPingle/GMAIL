package com.example.MyGmail.ui.label;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.MyGmail.R;
import com.example.MyGmail.data.models.Label;
import com.example.MyGmail.viewModel.LabelsViewModel;
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


public class AddLabelBottomSheet extends BottomSheetDialogFragment {

    private LabelsViewModel labelsViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottomsheet_add_label, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        labelsViewModel = new ViewModelProvider(requireActivity()).get(LabelsViewModel.class);

        TextInputLayout til = v.findViewById(R.id.tilLabelName);
        TextInputEditText et = v.findViewById(R.id.etLabelName);
        MaterialButton btnCreate = v.findViewById(R.id.btnCreate);
        MaterialButton btnCancel = v.findViewById(R.id.btnCancel);

        // Cache current labels for duplicate check
        final Set<String> existing = new HashSet<>();
        labelsViewModel.getLabels().observe(getViewLifecycleOwner(), list -> {
            existing.clear();
            if (list != null) {
                for (Label l : list) existing.add(l.getName().toLowerCase());
            }
        });

        final Set<String> system = new HashSet<>(Arrays.asList(
                "inbox","sent","starred","snoozed","spam","bin","trash","drafts","social","promotions","primary"
        ));

        btnCreate.setOnClickListener(v1 -> {
            String name = et.getText() == null ? "" : et.getText().toString().trim();
            til.setError(null);

            if (TextUtils.isEmpty(name)) {
                til.setError(getString(R.string.error_required));
                return;
            }
            if (name.length() > 30) {
                til.setError(getString(R.string.error_too_long));
                return;
            }
            String key = name.toLowerCase();
            if (system.contains(key)) {
                til.setError(getString(R.string.error_system_label));
                return;
            }
            if (existing.contains(key)) {
                til.setError(getString(R.string.error_label_exists));
                return;
            }
            btnCreate.setEnabled(false);

            // Call backend via repository; Room will update and observers will refresh the drawer.
            labelsViewModel.createLabel(name, new Callback<Label>() {
                @Override
                public void onResponse(@NonNull Call<Label> call, @NonNull Response<Label> response) {
                    btnCreate.setEnabled(true);
                    if (response.isSuccessful() && response.body() != null) {
                        dismiss();
                    } else {
                        til.setError(response.code() == 409 ? getString(R.string.error_label_exists) : "create failed: (" + response.code() + ")");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Label> call, @NonNull Throwable t) {
                    btnCreate.setEnabled(true);
                    til.setError(t.getMessage() == null ? "create failed" : t.getMessage());

                }
            });
        });

        btnCancel.setOnClickListener(v12 -> dismiss());
    }
}