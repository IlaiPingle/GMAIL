// java
package com.example.androidproject.ui.email;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.androidproject.R;
import com.example.androidproject.data.models.Mail;
import com.example.androidproject.data.remote.net.Resource;
import com.example.androidproject.ui.BaseActivity;
import com.example.androidproject.viewModel.MailsViewModel;
import com.google.android.material.appbar.MaterialToolbar;

public class ComposeEmailActivity extends BaseActivity {

    // ===== Keys =====
    static final String EXTRA_DRAFT_ID = "draft_id";
    private static final String K_TO   = "state_to";
    private static final String K_SUB  = "state_sub";
    private static final String K_BODY = "state_body";
    private static final String K_DRAFT= "state_draft";

    // ===== UI =====
    private MaterialToolbar toolbar;
    private EditText toField, subjectField, bodyField;
    @Nullable private MenuItem sendMenuItem;

    // ===== State =====
    private MailsViewModel viewModel;
    @Nullable private String draftId = null;
    private boolean isCreatingDraft = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_email);

        toolbar = findViewById(R.id.composeToolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(v -> handleExit());
        toolbar.setOnMenuItemClickListener(this::onToolbarMenuItemClick);
        toolbar.post(() -> {
            sendMenuItem = toolbar.getMenu().findItem(R.id.menu_send);
            updateSendEnabled();
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override public void handleOnBackPressed() { handleExit(); }
        });

        toField      = findViewById(R.id.editTextTo);
        subjectField = findViewById(R.id.editTextSubject);
        bodyField    = findViewById(R.id.editTextBody);

        viewModel = new ViewModelProvider(this).get(MailsViewModel.class);

        if (savedInstanceState != null) {
            toField.setText(savedInstanceState.getString(K_TO, ""));
            subjectField.setText(savedInstanceState.getString(K_SUB, ""));
            bodyField.setText(savedInstanceState.getString(K_BODY, ""));
            draftId = savedInstanceState.getString(K_DRAFT, null);
        }

        SimpleWatcher firstEditWatcher = new SimpleWatcher(() -> {
            ensureDraftCreated();
            updateSendEnabled();
        });
        toField.addTextChangedListener(firstEditWatcher);
        subjectField.addTextChangedListener(firstEditWatcher);
        bodyField.addTextChangedListener(firstEditWatcher);

        toField.setOnEditorActionListener((v, actionId, e) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) { attemptSend(); return true; }
            return false;
        });

        Intent in = getIntent();
        if (in != null && in.hasExtra(EXTRA_DRAFT_ID)) {
            draftId = in.getStringExtra(EXTRA_DRAFT_ID);
            viewModel.getMail(draftId).observe(this, res -> {
                if (res == null) return;
                switch (res.getStatus()) {
                    case SUCCESS:
                        if (res.getData() != null) bindMail(res.getData());
                        break;
                    case ERROR:
                        toast(res.getMessage());
                        if (res.getData() != null) bindMail(res.getData());
                        break;
                    case LOADING:
                        break;
                }
            });
            viewModel.refreshMail(draftId);
        }

        updateSendEnabled();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.compose_menu, menu);
        sendMenuItem = menu.findItem(R.id.menu_send);
        updateSendEnabled();
        return true;
    }

    private boolean onToolbarMenuItemClick(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_send) {
            attemptSend();
            return true;
        } else if (id == R.id.menu_save_draft) {
            saveAndFinish();
            return true;
        } else if (id == R.id.menu_discard) {
            handleExit();
            return true;
        }
        return false;
    }

    // ===== Exit =====
    private void handleExit() {
        if (!hasAnyContent()) { finish(); return; }
        new AlertDialog.Builder(this)
                .setMessage("Save draft?")
                .setPositiveButton("Save", (d, w) -> saveAndFinish()) // לא קוראים finish פה; saveAndFinish ידאג לכך
                .setNegativeButton("Discard", (d, w) -> { clearCompose(); finish(); })
                .setNeutralButton("Cancel", null)
                .show();
    }

    private void saveAndFinish() {
        if (draftId == null) {
            createDraft(() -> { toast("Draft saved"); finish(); });
        } else {
            Mail mail = buildMailFromFields();
            mail.setId(draftId);
            observeOnce(viewModel.updateMail(mail), res -> {
                if (res == null) { toast("Draft save error"); return; }
                switch (res.getStatus()) {
                    case SUCCESS:
                        toast("Draft saved");
                        clearCompose();
                        finish();
                        break;
                    case ERROR:
                        toast("Draft save failed: " + res.getMessage());
                        break;
                }
            });
        }
    }

    // ===== Send =====
    private void attemptSend() {
        String to = safe(toField.getText()).trim();
        if (to.isEmpty()) {
            Toast.makeText(this, "Please enter a valid recipient", Toast.LENGTH_SHORT).show();
            return;
        }
        setSendEnabled(false);
        if (draftId == null) {
            createDraft(this::sendNow);
        } else {
            sendNow();
        }
    }

    private void sendNow() {
        Mail mail = buildMailFromFields();
        mail.setId(draftId);
        observeOnce(viewModel.sendMail(mail), res -> {
            if (res == null) { toast("Send error"); setSendEnabled(true); return; }
            switch (res.getStatus()) {
                case SUCCESS:
                    toast("Email sent");
                    clearCompose();
                    finish();
                    break;
                case ERROR:
                    toast("Send failed: " + res.getMessage());
                    setSendEnabled(true);
                    break;
            }
        });
    }

    // ===== Draft creation =====
    private void ensureDraftCreated() {
        if (draftId != null) return;
        if (!hasAnyContent()) return;
        if (isCreatingDraft) return;
        createDraft(null);
    }

    private void createDraft(@Nullable Runnable onSuccessThen) {
        isCreatingDraft = true;
        Mail draft = buildMailFromFields();
        observeOnce(viewModel.createDraft(draft), res -> {
            isCreatingDraft = false;
            if (res == null) { toast("Draft creation error"); return; }
            switch (res.getStatus()) {
                case SUCCESS:
                    if (res.getData() != null) {
                        draftId = res.getData().getId();
                        if (onSuccessThen != null) onSuccessThen.run();
                    } else {
                        toast("Draft save failed: empty response");
                    }
                    break;
                case ERROR:
                    toast("Draft save failed: " + res.getMessage());
                    break;
            }
        });
    }

    // ===== Helpers =====
    private void bindMail(Mail mail) {
        if (mail == null) return;
        if (TextUtils.isEmpty(safe(toField.getText())))      toField.setText(safe(mail.getReceiver()));
        if (TextUtils.isEmpty(safe(subjectField.getText()))) subjectField.setText(safe(mail.getSubject()));
        if (TextUtils.isEmpty(safe(bodyField.getText())))    bodyField.setText(safe(mail.getBody()));
        updateSendEnabled();
    }

    private Mail buildMailFromFields() {
        Mail m = new Mail();
        m.setReceiver(safe(toField.getText()).trim());
        m.setSubject(safe(subjectField.getText()));
        m.setBody(safe(bodyField.getText()));
        return m;
    }

    private void setSendEnabled(boolean enabled) {
        if (sendMenuItem != null) {
            sendMenuItem.setEnabled(enabled);
            if (sendMenuItem.getIcon() != null)
                sendMenuItem.getIcon().setAlpha(enabled ? 255 : 100);
        }
    }

    private void updateSendEnabled() {
        boolean hasRecipient = !TextUtils.isEmpty(safe(toField.getText()).trim());
        if (sendMenuItem != null) {
            sendMenuItem.setEnabled(hasRecipient);
            if (sendMenuItem.getIcon() != null)
                sendMenuItem.getIcon().setAlpha(hasRecipient ? 255 : 100);
        }
    }

    private boolean hasAnyContent() {
        return !TextUtils.isEmpty(safe(toField.getText()).trim())
                || !TextUtils.isEmpty(safe(subjectField.getText()))
                || !TextUtils.isEmpty(safe(bodyField.getText()));
    }

    private void clearCompose() {
        toField.setText("");
        subjectField.setText("");
        bodyField.setText("");
        draftId = null;
        isCreatingDraft = false;
        updateSendEnabled();
    }

    private static class SimpleWatcher implements TextWatcher {
        private final Runnable onChange;
        SimpleWatcher(Runnable onChange) { this.onChange = onChange; }
        @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
        @Override public void onTextChanged(CharSequence s, int st, int b, int c) { onChange.run(); }
        @Override public void afterTextChanged(Editable s) {}
    }

    private void toast(String m) {
        Toast.makeText(this, m, Toast.LENGTH_SHORT).show();
    }

    private <T> void observeOnce(LiveData<Resource<T>> liveData, Observer<Resource<T>> observer) {
        liveData.observe(this, new Observer<>() {
            @Override public void onChanged(Resource<T> resource) {
                if (resource == null) return;
                switch (resource.getStatus()) {
                    case LOADING: break;
                    case ERROR:
                    case SUCCESS:
                        liveData.removeObserver(this);
                        observer.onChanged(resource);
                        break;
                }
            }
        });
    }

    private static String safe(@Nullable CharSequence cs) {
        return cs == null ? "" : cs.toString();
    }

    // ===== Save/Restore =====
    @Override
    protected void onSaveInstanceState(@NonNull Bundle out) {
        super.onSaveInstanceState(out);
        out.putString(K_TO,   safe(toField.getText()));
        out.putString(K_SUB,  safe(subjectField.getText()));
        out.putString(K_BODY, safe(bodyField.getText()));
        out.putString(K_DRAFT, draftId);
    }
}
