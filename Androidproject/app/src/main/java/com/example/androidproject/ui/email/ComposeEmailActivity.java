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
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import com.example.androidproject.R;
import com.example.androidproject.data.models.Mail;
import com.example.androidproject.data.remote.net.Resource;
import com.example.androidproject.ui.BaseActivity;
import com.example.androidproject.viewModel.MailsViewModel;
import com.google.android.material.appbar.MaterialToolbar;

/**
 * Activity for composing a new email.
 * Features:
 * - Recipient input with chips (To field)
 * - Subject and body input
 * - Save draft to backend when "Save" is clicked
 * - Send email, creating draft on backend if needed
 * - Discard draft with confirmation
 * - Exit with confirmation if draft has content
 */
public class ComposeEmailActivity extends BaseActivity {
    // UI elements
    private MaterialToolbar toolbar;
    private AutoCompleteTextView editTextTo;

    private EditText toField, subjectField, bodyField;
    @Nullable
    private MenuItem sendMenuItem;
    static final String EXTRA_DRAFT_ID = "draft_id";
    private MailsViewModel viewModel;
    @Nullable
    private String draftId = null; // Set when draft is created on server

    // ViewModel for email operations
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_email);

        toolbar = findViewById(R.id.composeToolbar);
        setSupportActionBar(toolbar);

        // Toolbar navigation (X) -> Gmail-like exit
        toolbar.setNavigationOnClickListener(v -> handleExit());
        // Menu is defined in XML (app:menu). Handle clicks here.
        toolbar.setOnMenuItemClickListener(this::onToolbarMenuItemClick);
        // Menu may not be ready immediately; post to read it safely.
        toolbar.post(() -> {
            sendMenuItem = toolbar.getMenu().findItem(R.id.menu_send);
            updateSendEnabled();
        });

        // Back gesture -> Gmail-like exit
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                handleExit();
            }
        });

        // Bind views
        toField = findViewById(R.id.editTextTo);
        subjectField = findViewById(R.id.editTextSubject);
        bodyField = findViewById(R.id.editTextBody);

        // ViewModel
        viewModel = new ViewModelProvider(this).get(MailsViewModel.class);

        SimpleWatcher firstEditWatcher = new SimpleWatcher(() -> {
            ensureDraftCreated();
            updateSendEnabled();
        });
        toField.addTextChangedListener(firstEditWatcher);
        subjectField.addTextChangedListener(firstEditWatcher);
        bodyField.addTextChangedListener(firstEditWatcher);

        toField.setOnEditorActionListener((v, actionId, e) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                attemptSend();
                return true;
            }
            return false;
        });

        Intent in = getIntent();
        if (in != null && in.hasExtra(EXTRA_DRAFT_ID)) {
            draftId = in.getStringExtra(EXTRA_DRAFT_ID);
            viewModel.getMail(draftId).observe(this, resource -> {
                Mail mail = getMailResult(resource);
                if (mail != null) {
                    toField.setText(mail.getReceiver());
                    subjectField.setText(mail.getSubject());
                    bodyField.setText(mail.getBody());
                }
                viewModel.refreshMail(draftId);
            });
        } else {
            updateSendEnabled();
        }
    }

    /**
     * Inflates the menu; this adds items to the action bar if it is present.
     *
     * @param menu The options menu in which you place your items.
     * @return true for the menu to be displayed; false otherwise.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.compose_menu, menu);
        sendMenuItem = menu.findItem(R.id.menu_send);
        updateSendEnabled();
        return true;
    }

    /**
     * Handles toolbar menu item clicks.
     *
     * @param item The menu item that was clicked.
     * @return true if the click was handled; false otherwise.
     */
    private boolean onToolbarMenuItemClick(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_send) {
            attemptSend();
            return true;
        } else if (id == R.id.menu_save_draft) {
            saveAndFinish();
            Toast.makeText(this, "Draft saved", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.menu_discard) {
            handleExit();
            return true;
        }
        return false;
    }
    private Mail getMailResult(Resource<Mail> res) {
        if (res == null) return null;
        switch (res.getStatus()) {
            case SUCCESS:
                return res.getData();
            case ERROR:
                toast(res.getMessage());
                return res.getData(); // May be null
            case LOADING:
                return res.getData(); // May be null
            default:
                return null;
        }
    }

// =================EXIT HANDLING================

    /**
     * Handles exit action (toolbar X or back gesture).
     * If draft is empty, exits immediately.
     * If draft has content, shows dialog to Save/Discard/Cancel.
     * Saves draft if "Save" is chosen, clears if "Discard", does nothing if "Cancel".
     */
    private void handleExit() {
        if (!hasAnyContent()) {
            finish();
            return;
        }
        new AlertDialog.Builder(this)
                .setMessage("Save draft?")
                .setPositiveButton("Save", (d, w) -> {
                    saveAndFinish();
                    finish();
                })
                .setNegativeButton("Discard", (d, w) -> {
                    clearCompose();
                    finish();
                })
                .setNeutralButton("Cancel", null)
                .show();
    }

    private void saveAndFinish() {
        if (draftId == null) {
            createDraft(this::finish);
        } else {
            Mail mail = buildMailFromFields();
            mail.setId(draftId);
            viewModel.updateMail(mail);
            finish();
        }
    }

//    ================= SENDING =================

    /**
     * Checks if the draft is empty (no recipients, subject, body, or attachments).
     */
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
        viewModel.sendMail(mail).observe(this, res -> {
            if (res == null) {
                toast("Send error");
                setSendEnabled(true);
                return;
            }
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
                case LOADING:
                    // Ignore loading state
                    break;
            }
        });
    }

    //   =============== DRAFT CREATION =================
    private void ensureDraftCreated() {
        if (draftId != null) return;
        createDraft(null);
    }

    private void createDraft(@Nullable Runnable onSuccessThen) {
        Mail draft = buildMailFromFields();
        viewModel.createDraft(draft).observe(this, res -> {
            Mail mail = getMailResult(res);
            if (mail != null) {
                draftId = mail.getId();
                if (onSuccessThen != null) onSuccessThen.run();
            } else {
                toast("Draft save failed: " + (res != null ? res.getMessage() : "unknown error"));
            }
        });
    }

//  ================= HELPERS =================

    /**
     * Builds a Mail object from the current compose fields.
     *
     * @return The Mail object representing the current draft.
     */
    private Mail buildMailFromFields() {
        Mail mail = new Mail();
        mail.setReceiver(safe(toField.getText()).trim());
        mail.setSubject(safe(subjectField.getText()));
        mail.setBody(safe(bodyField.getText()));
        return mail;
    }

    /**
     * Enables or disables the Send menu item.
     *
     * @param enabled true to enable, false to disable.
     */
    private void setSendEnabled(boolean enabled) {
        if (sendMenuItem != null) {
            sendMenuItem.setEnabled(enabled);
            if (sendMenuItem.getIcon() != null) {
                sendMenuItem.getIcon().setAlpha(enabled ? 255 : 100);
            }
        }
    }
    private void updateSendEnabled() {
        boolean canSend = TextUtils.isEmpty(safe(toField.getText()).trim());
        if (sendMenuItem != null) {
            sendMenuItem.setEnabled(canSend);
            if (sendMenuItem.getIcon() != null) sendMenuItem.getIcon().setAlpha(canSend ? 255 : 100);
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
    }

    /**
     * Simple TextWatcher implementation that runs a Runnable on text change.
     * Used for scheduling autosave and updating Send button state.
     */
    private static class SimpleWatcher implements TextWatcher {
        private final Runnable onChange;

        SimpleWatcher(Runnable onChange) {
            this.onChange = onChange;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int st, int c, int a) {
        }

        @Override
        public void onTextChanged(CharSequence s, int st, int b, int c) {
            onChange.run();
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }

    private void toast(String m) {
        Toast.makeText(this, m, Toast.LENGTH_SHORT).show();
    }


    /**
     * Safely converts a CharSequence to String, returning empty string if null.
     *
     * @param cs The CharSequence to convert.
     * @return The resulting String, or "" if input is null.
     */
    private static String safe(@Nullable CharSequence cs) {
        return cs == null ? "" : cs.toString();
    }
}