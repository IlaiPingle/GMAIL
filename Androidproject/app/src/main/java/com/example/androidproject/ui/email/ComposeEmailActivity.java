// java
package com.example.androidproject.ui.email;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.androidproject.R;
import com.example.androidproject.data.models.Mail;
import com.example.androidproject.viewModel.MailsViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

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
public class ComposeEmailActivity extends AppCompatActivity {
    // UI elements
    private MaterialToolbar toolbar;
    private AutoCompleteTextView editTextTo;
    private ChipGroup chipGroupTo;
    private EditText editTextSubject, editTextBody;
    @Nullable private MenuItem sendMenuItem;

    // Draft autosave
//    private final Handler autoSaveHandler = new Handler(Looper.getMainLooper());
//    private static final long AUTO_SAVE_MS = 5000L;
    private static final String PREFS = "compose_prefs";
    private static final String DRAFT_TO = "draft_to";
    private static final String DRAFT_SUBJECT = "draft_subject";
    private static final String DRAFT_BODY = "draft_body";
    @Nullable private String draftId = null; // Set when draft is created on server

    // ViewModel for email operations
    private MailsViewModel viewModel;

//    // Snapshot for Undo
//    private static class DraftSnapshot {
//        List<String> to = new ArrayList<>();
//        String subject = "";
//        CharSequence body = "";
//    }

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
            @Override public void handleOnBackPressed() { handleExit(); }
        });

        // Bind views
        editTextTo = findViewById(R.id.editTextTo);
        chipGroupTo = findViewById(R.id.chipGroupTo);
        editTextSubject = findViewById(R.id.editTextSubject);
        editTextBody = findViewById(R.id.editTextBody);

        // ViewModel
        viewModel = new ViewModelProvider(this).get(MailsViewModel.class);

        // Recipient chips
        setupRecipientChipField(editTextTo, chipGroupTo);

        // Watchers for autosave and Send enablement
//        editTextSubject.addTextChangedListener(new SimpleTextWatcher(this::scheduleAutoSave));
//        editTextBody.addTextChangedListener(new SimpleTextWatcher(this::scheduleAutoSave));
        chipGroupTo.setOnHierarchyChangeListener(simpleChipHierarchyWatcher());

        // Load draft
        loadDraft();
        updateSendEnabled();

        // If editing existing draft, load its data
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("draft_id")) {
            draftId = intent.getStringExtra("draft_id");
            restoreChips(intent.getStringExtra("to"), chipGroupTo);
            editTextSubject.setText(intent.getStringExtra("subject"));
            editTextBody.setText(intent.getStringExtra("body"));
        }
    }

    /**
     * Inflates the menu; this adds items to the action bar if it is present.
     * @param menu The options menu in which you place your items.
     * @return true for the menu to be displayed; false otherwise.
     */
    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.compose_menu, menu);
        sendMenuItem = menu.findItem(R.id.menu_send);
        updateSendEnabled();
        return true;
    }

    /**
     * Handles toolbar menu item clicks.
     * @param item The menu item that was clicked.
     * @return true if the click was handled; false otherwise.
     */
    private boolean onToolbarMenuItemClick(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_send) {
            attemptSend();
            return true;
        } else if (id == R.id.menu_save_draft) {
            saveDraftNow();
            Toast.makeText(this, "Draft saved", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.menu_discard) {
            confirmDiscard();
            return true;
        }
        return false;
    }

    /**
     * Handles exit action (toolbar X or back gesture).
     * If draft is empty, exits immediately.
     * If draft has content, shows dialog to Save/Discard/Cancel.
     * Saves draft if "Save" is chosen, clears if "Discard", does nothing if "Cancel".
     */
    private void handleExit() {
        if (isDraftEmpty()) {
            finish();
            return;
        }
        new AlertDialog.Builder(this)
                .setMessage("Save draft?")
                .setPositiveButton("Save", (d, w) -> { saveDraftNow(); finish(); })
                .setNegativeButton("Discard", (d, w) -> { clearCompose(); finish(); })
                .setNeutralButton("Cancel", null)
                .show();
    }

    /**
     * Checks if the draft is empty (no recipients, subject, body, or attachments).
     */
    private void attemptSend() {
        commitPendingEmail(editTextTo, chipGroupTo);
        List<String> toList = getChipsEmails(chipGroupTo);
        if (toList.isEmpty()) {
            Toast.makeText(this, "Add at least one recipient", Toast.LENGTH_SHORT).show();
            return;
        }
        String receiver = toList.get(0);
        createDraftIfNeededAndSend();
    }

    /**
     * Enables or disables the Send menu item.
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

    /**
     * Used to discard the current draft after user confirmation.
     */
    private void confirmDiscard() {
        new AlertDialog.Builder(this)
                .setMessage("Discard draft?")
                .setPositiveButton("Discard", (d, w) -> { clearCompose(); updateSendEnabled(); })
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Clears the compose fields and resets state.
     * Also clears the saved draft from SharedPreferences.
     */
    private void clearCompose() {
        chipGroupTo.removeAllViews();
        editTextTo.setText("");
        editTextSubject.setText("");
        editTextBody.setText("");
        updateSendEnabled();
    }

    /**
     * Sets up an AutoCompleteTextView to handle recipient chips.
     * @param field The AutoCompleteTextView for input.
     * @param group The ChipGroup to add chips to.
     */
    private void setupRecipientChipField(AutoCompleteTextView field, ChipGroup group) {
        // Commit on IME actions
        field.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE
                    || actionId == EditorInfo.IME_ACTION_SEND
                    || actionId == EditorInfo.IME_ACTION_NEXT) {
                commitPendingEmail(field, group);
                return true;
            }
            return false;
        });

        // Commit on separators
        final boolean[] editing = {false};
        field.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {
                if (editing[0]) return;
                String text = safe(s);
                // Commit on comma, semicolon, or space
                if (text.contains(",") || text.contains(";") || text.endsWith(" ")) {
                    String[] parts = text.split("[,;\\s]");
                    String last = parts.length == 0 ? "" : parts[parts.length - 1];
                    String candidate = text.substring(0, text.length() - last.length()).trim();
                    String email = candidate.replace(",", "").replace(";", "").trim();
                    if (!TextUtils.isEmpty(email)) {
                        editing[0] = true;
                        if (maybeAddChip(email, group)) {
                            field.setText("");
                        }
                        editing[0] = false;
                    }
                }
            }
            @Override public void afterTextChanged(Editable s) {
                updateSendEnabled();
//                scheduleAutoSave();
            }
        });

        field.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) commitPendingEmail(field, group);
        });
    }

    /**
     * Commits any pending email in the field to a chip in the group.
     * @param field The AutoCompleteTextView containing the email input.
     * @param group The ChipGroup to add the chip to.
     */
    private void commitPendingEmail(AutoCompleteTextView field, ChipGroup group) {
        String email = safe(field.getText()).trim();
        if (maybeAddChip(email, group)) {
            field.setText("");
        }
        updateSendEnabled();
//        scheduleAutoSave();
    }

    /**
     * Checks if it is possible to add a chip for the given email.
     * If valid, adds the chip to the group.
     * @param email The email address to add as a chip.
     * @param group The ChipGroup to add the chip to.
     * @return true if a chip was added, false otherwise.
     */
    private boolean maybeAddChip(String email, ChipGroup group) {
        if (TextUtils.isEmpty(email)) return false;
        addChip(email, group);
        return true;
    }

    /**
     * Adds a chip with the given email to the specified ChipGroup.
     * @param email The email address to display on the chip.
     * @param group The ChipGroup to add the chip to.
     */
    private void addChip(String email, ChipGroup group) {
        Chip chip = new Chip(this);
        chip.setText(email);
        chip.setCloseIconVisible(true);
        chip.setOnCloseIconClickListener(v -> {
            group.removeView(chip);
            updateSendEnabled();
//            scheduleAutoSave();
        });
        group.addView(chip);
        updateSendEnabled();
    }

    /**
     * Restores chips from a comma-separated string of emails.
     * @param group The ChipGroup to add chips to.
     * @return List of emails added as chips.
     */
    private List<String> getChipsEmails(ChipGroup group) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < group.getChildCount(); i++) {
            View v = group.getChildAt(i);
            if (v instanceof Chip) list.add(((Chip) v).getText().toString());
        }
        return list;
    }

    // Drafts

    /**
     * Creates a draft on the backend if needed, then sends the email.
     * If draft already exists (draftId != null), skips creation and sends directly.
     * Handles API responses and errors, updating UI accordingly.
     * Disables Send button during operation to prevent duplicates.
     */
    private void createDraftIfNeededAndSend() {
        if (draftId != null) {
            sendEmailWithDraftId(draftId);
            return;
        }
        Mail draft = buildMailFromFields();
        viewModel.createDraft(draft);
    }

    /**
     * Sends the email using the existing draft ID.
     * Handles API responses and errors, updating UI accordingly.
     * Disables Send button during operation to prevent duplicates.
     * @param draftId The ID of the draft to send.
     */
    private void sendEmailWithDraftId(String draftId) {
        List<String> toList = getChipsEmails(chipGroupTo);
        if (toList.isEmpty()) {
            Toast.makeText(this, "Add at least one recipient", Toast.LENGTH_SHORT).show();
            setSendEnabled(true);
            return;
        }
        Mail mail = buildMailFromFields();
        mail.setId(draftId);
        viewModel.sendMail(mail);
        setSendEnabled(true);
        clearCompose();
        Toast.makeText(this, "Email sent", Toast.LENGTH_SHORT).show();
    }

    /**
     * Schedules an autosave of the draft after a delay.
     * Cancels any previously scheduled autosave to avoid excessive saves.
     */
//    private void scheduleAutoSave() {
//        autoSaveHandler.removeCallbacksAndMessages(null);
//        autoSaveHandler.postDelayed(this::saveDraftNow, AUTO_SAVE_MS);
//    }

    /**
     * Saves the current draft state.
     * If draftId is set, updates the draft on the backend.
     * If no draftId, saves to SharedPreferences locally.
     */
    private void saveDraftNow() {
        if (draftId != null) {
            // Update draft on backend
            Mail mail = buildMailFromFields();
            mail.setId(draftId);
            viewModel.updateMail(mail);
            Toast.makeText(this, "Draft updated", Toast.LENGTH_SHORT).show();
        } else {
            SharedPreferences sp = getSharedPreferences(PREFS, Context.MODE_PRIVATE);
            // Include chips + any pending text
            String to = joinCsv(getChipsEmails(chipGroupTo), safe(editTextTo.getText()));
            sp.edit()
                    .putString(DRAFT_TO, to)
                    .putString(DRAFT_SUBJECT, safe(editTextSubject.getText()))
                    .putString(DRAFT_BODY, safe(editTextBody.getText()))
                    .apply();
        }
    }

    /**
     * Builds a Mail object from the current compose fields.
     * @return The Mail object representing the current draft.
     */
    private Mail buildMailFromFields() {
        Mail mail = new Mail();
        mail.setReceiver(TextUtils.join(",", getChipsEmails(chipGroupTo)));
        mail.setSubject(editTextSubject.getText().toString());
        mail.setBody(editTextBody.getText().toString());
        return mail;
    }

    /**
     * Loads the draft from SharedPreferences into the compose fields.
     * Called during onCreate to restore any unsent draft.
     */
    private void loadDraft() {
        SharedPreferences sp = getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        restoreChips(sp.getString(DRAFT_TO, ""), chipGroupTo);
        editTextSubject.setText(sp.getString(DRAFT_SUBJECT, ""));
        editTextBody.setText(sp.getString(DRAFT_BODY, ""));
    }

    /**
     * Restores chips from a comma-separated string of emails.
     * @param csv Comma-separated emails.
     * @param group The ChipGroup to add chips to.
     */
    private void restoreChips(String csv, ChipGroup group) {
        if (TextUtils.isEmpty(csv)) return;
        String[] parts = csv.split(",");
        for (String p : parts) {
            String email = p.trim();
            if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) addChip(email, group);
        }
    }

    // Helpers

    /**
     * Updates the Send button enabled state based on whether there is at least one recipient.
     * Called whenever recipient chips or fields change.
     * Enables Send if any of To/Cc/Bcc has at least one valid email.
     * Disables Send otherwise.
     */
    private void updateSendEnabled() {
        boolean hasTo = !getChipsEmails(chipGroupTo).isEmpty()
                || Patterns.EMAIL_ADDRESS.matcher(safe(editTextTo.getText()).trim()).matches();
        boolean hasAnyRecipient = hasTo;
        if (sendMenuItem != null) {
            sendMenuItem.setEnabled(hasAnyRecipient);
            if (sendMenuItem.getIcon() != null) {
                sendMenuItem.getIcon().setAlpha(hasAnyRecipient ? 255 : 100);
            }
        }
    }

    /**
     * Checks if the current draft is empty (no recipients, subject, body, or attachments).
     * @return true if the draft is empty, false otherwise.
     */
    private boolean isDraftEmpty() {
        boolean noRecipients = getChipsEmails(chipGroupTo).isEmpty()
                && TextUtils.isEmpty(safe(editTextTo.getText()));
        boolean noSubject = TextUtils.isEmpty(safe(editTextSubject.getText()));
        boolean noBody = TextUtils.isEmpty(safe(editTextBody.getText()));
        return noRecipients && noSubject && noBody;
    }

    /**
     * Simple hierarchy change listener for ChipGroups to watch for chip additions/removals.
     * Schedules an autosave and updates Send button state on changes.
     * @return The listener instance.
     */
    private ChipGroup.OnHierarchyChangeListener simpleChipHierarchyWatcher() {
        return new ChipGroup.OnHierarchyChangeListener() {
            @Override public void onChildViewAdded(View parent, View child) {  updateSendEnabled(); }
            @Override public void onChildViewRemoved(View parent, View child) {  updateSendEnabled(); }
        };
    }

    /**
     * Simple TextWatcher implementation that runs a Runnable on text change.
     * Used for scheduling autosave and updating Send button state.
     */
    private static class SimpleTextWatcher implements TextWatcher {
        private final Runnable onChange;
        SimpleTextWatcher(Runnable onChange) { this.onChange = onChange; }
        @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
        @Override public void onTextChanged(CharSequence s, int st, int b, int c) { onChange.run(); }
        @Override public void afterTextChanged(Editable s) {}
    }

    /**
     * Safely converts a CharSequence to String, returning empty string if null.
     * @param cs The CharSequence to convert.
     * @return The resulting String, or "" if input is null.
     */
    private static String safe(@Nullable CharSequence cs) {
        return cs == null ? "" : cs.toString();
    }

    /**
     * Joins a list of strings into a CSV string, including any pending text.
     * Trims whitespace and ignores empty strings.
     * @param chips List of existing chip strings.
     * @param pending Any pending text to include.
     * @return Comma-separated string of all non-empty trimmed strings.
     */
    private static String joinCsv(List<String> chips, String pending) {
        List<String> all = new ArrayList<>(chips);
        if (!TextUtils.isEmpty(pending)) all.add(pending.trim());
        return TextUtils.join(",", all);
    }
}