
package com.example.androidproject.ui.email;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.androidproject.R;
import com.example.androidproject.data.models.Mail;
import com.example.androidproject.data.remote.net.Resource;
import com.example.androidproject.ui.BaseActivity;
import com.example.androidproject.viewModel.MailsViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


import java.util.List;

public class EmailDetailActivity extends BaseActivity {
    public static final String EXTRA_MAIL_ID = "extra_mail_id";
    public static final String EXTRA_ENTRY_LABEL = "extra_entry_label";
    private MailsViewModel viewModel;
    @Nullable
    private Mail currentMail;
    private @Nullable String entryLabel;
    private TextView tvFrom, tvTo, tvSubject, tvBody, tvLabels, tvSpamWarning;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_detail);

        String mailId = getIntent().getStringExtra(EXTRA_MAIL_ID);
        entryLabel = getIntent().getStringExtra(EXTRA_ENTRY_LABEL);
        if (entryLabel == null) entryLabel = "all";

        if (TextUtils.isEmpty(mailId)) {
            Toast.makeText(this, "Missing mail id", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        MaterialToolbar toolbar = findViewById(R.id.detailToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        tvFrom = findViewById(R.id.tvFrom);
        tvTo = findViewById(R.id.tvTo);
        tvSubject = findViewById(R.id.tvSubject);
        tvBody = findViewById(R.id.tvBody);
        tvLabels = findViewById(R.id.tvLabels);
        tvSpamWarning = findViewById(R.id.tvSpamWarning);

        viewModel = new ViewModelProvider(this).get(MailsViewModel.class);
        viewModel.getMail(mailId).observe(this, res -> {
            if (res == null) return;
            switch (res.getStatus()) {
                case LOADING:
                    break;
                case ERROR:
                    if (res.getData() == null) {
                        Toast.makeText(this,
                                res.getMessage() != null ? res.getMessage() : "Failed to load mail",
                                Toast.LENGTH_SHORT).show();
                    }
                    bindMail(res.getData());
                    break;
                case SUCCESS:
                    bindMail(res.getData());
                    break;
            }
        });
        viewModel.refreshMail(mailId);

        FloatingActionButton deleteButton = findViewById(R.id.fabDelete);
        deleteButton.setOnClickListener(v -> {
            if (currentMail == null) return;
            v.setEnabled(false);
            boolean isInBin = hasLabel(currentMail, "bin");
            boolean isInSpam = hasLabel(currentMail, "spam");

            if (("bin".equalsIgnoreCase(entryLabel) && isInBin) ||
                    ("spam".equalsIgnoreCase(entryLabel) && isInSpam)) {
                observeOnce(viewModel.deleteMail(currentMail), status -> {
                    if (status != null && status.getStatus() == Resource.Status.ERROR) {
                        Toast.makeText(this,
                                status.getMessage() != null ? status.getMessage() : "Delete failed",
                                Toast.LENGTH_SHORT).show();
                        v.setEnabled(true);
                    } else {
                        Toast.makeText(this, "Mail deleted permanently", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    }
                });
                return;
            }
            observeOnce(viewModel.addLabelToMail(currentMail, "bin"), status -> {
                if (status != null && status.getStatus() == Resource.Status.ERROR) {
                    Toast.makeText(this,
                            status.getMessage() != null ? status.getMessage() : "Move to bin failed",
                            Toast.LENGTH_SHORT).show();
                    v.setEnabled(true);
                } else {
                    Toast.makeText(this, "Moved to Bin", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                }
            });

        });

        findViewById(R.id.btnStar).setOnClickListener(v -> {
            if (currentMail == null) return;
            v.setEnabled(false);
            boolean currentlyStarred = hasLabel(currentMail, "starred");
            observeOnce(viewModel.toggleStar(currentMail.getId(), currentlyStarred), st -> {
                if (st != null && st.getStatus() == Resource.Status.ERROR) {
                    Toast.makeText(this,
                            st.getMessage() != null ? st.getMessage() : "Failed to update star",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, currentlyStarred ? "Unstarred" : "Starred", Toast.LENGTH_SHORT).show();
                }
                v.setEnabled(true);
            });
        });

        findViewById(R.id.btnMarkUnread).setOnClickListener(v -> {
            if (currentMail == null) return;
            v.setEnabled(false);
            observeOnce(viewModel.addLabelToMail(currentMail, "unread"), st -> {
                if (st != null && st.getStatus() == Resource.Status.ERROR) {
                    Toast.makeText(this,
                            st.getMessage() != null ? st.getMessage() : "Failed to mark unread",
                            Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(this, "Marked as unread", Toast.LENGTH_SHORT).show();
                }
                v.setEnabled(true);
            });
        });

        findViewById(R.id.btnSpam).setOnClickListener(v -> {
            if (currentMail == null) return;
            v.setEnabled(false);
            if (hasLabel(currentMail, "spam")) {
                observeOnce(viewModel.removeMailFromSpam(currentMail), st -> {
                    if (st != null && st.getStatus() == Resource.Status.ERROR) {
                        Toast.makeText(this,
                                st.getMessage() != null ? st.getMessage() : "Failed to remove from spam",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Removed from spam", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    v.setEnabled(true);
                });
            } else {
                observeOnce(viewModel.markMailAsSpam(currentMail),st -> {
                    if (st != null && st.getStatus() == Resource.Status.ERROR) {
                        Toast.makeText(this,
                                st.getMessage() != null ? st.getMessage() : "Failed to mark as spam",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Marked as spam", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    }
                    v.setEnabled(true);
                });
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void bindMail(@Nullable Mail mail) {
        currentMail = mail;
        if (mail == null) return;

        tvFrom.setText(safe(mail.getSender()));
        tvTo.setText(safe(mail.getReceiver()));
        tvSubject.setText(safe(mail.getSubject()));
        tvBody.setText(safe(mail.getBody()));

        List<String> labels = mail.getLabels();
        tvLabels.setText(labels == null ? "" : TextUtils.join(", ", labels));

        boolean isStarred = hasLabel(mail, "starred");
        View starBtn = findViewById(R.id.btnStar);
        if (starBtn instanceof MaterialButton) {
            ((MaterialButton) starBtn)
                    .setText(isStarred ? "Unstar" : "Star");
        }
        boolean isSpam = hasLabel(mail, "spam");
        if (tvSpamWarning != null) {
            tvSpamWarning.setVisibility(isSpam ? View.VISIBLE : View.GONE);
        }
    }

    private boolean hasLabel(Mail mail, String label) {
        return mail.getLabels() != null && mail.getLabels().contains(label);
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }

    private <T> void observeOnce(LiveData<Resource<T>> liveData,
                                 Observer<Resource<T>> observer) {
        liveData.observe(this, new Observer<>() {
            @Override
            public void onChanged(Resource<T> resource) {
                if (resource == null) return;
                switch (resource.getStatus()) {
                    case LOADING:
                        break;
                    case ERROR:
                    case SUCCESS:
                        liveData.removeObserver(this);
                        observer.onChanged(resource);
                        break;
                }
            }
        });
    }
}
