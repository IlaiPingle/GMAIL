
package com.example.androidproject.ui.email;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.androidproject.R;
import com.example.androidproject.data.models.Mail;
import com.example.androidproject.viewModel.MailsViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class EmailDetailActivity extends AppCompatActivity {
    public static final String EXTRA_MAIL_ID = "extra_mail_id";

    private MailsViewModel viewModel;
    @Nullable private Mail currentMail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_detail);

        String mailId = getIntent().getStringExtra(EXTRA_MAIL_ID);
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

        TextView tvFrom = findViewById(R.id.tvFrom);
        TextView tvTo = findViewById(R.id.tvTo);
        TextView tvSubject = findViewById(R.id.tvSubject);
        TextView tvBody = findViewById(R.id.tvBody);
        TextView tvLabels = findViewById(R.id.tvLabels);
        TextView tvSpamWarning = findViewById(R.id.tvSpamWarning);

        viewModel = new ViewModelProvider(this).get(MailsViewModel.class);
        viewModel.getMail(mailId).observe(this, mail -> {
            currentMail = mail;
            if (mail == null) return;
            tvFrom.setText(mail.getSender() == null ? "" : mail.getSender());
            tvTo.setText(mail.getReceiver() == null ? "" : mail.getReceiver());
            tvSubject.setText(mail.getSubject() == null ? "" : mail.getSubject());
            tvBody.setText(mail.getBody() == null ? "" : mail.getBody());
            tvLabels.setText(mail.getLabels() == null ? "" : TextUtils.join(", ", mail.getLabels()));
            View starBtn = findViewById(R.id.btnStar);
            boolean isStarred = mail.getLabels() != null && mail.getLabels().contains("starred");
            if (starBtn instanceof com.google.android.material.button.MaterialButton) {
                ((com.google.android.material.button.MaterialButton) starBtn).setText(isStarred ? "Unstar" : "Star");
            }
            boolean isSpam = mail.getLabels() != null && mail.getLabels().contains("spam");
            if (tvSpamWarning != null) {
                tvSpamWarning.setVisibility(isSpam ? View.VISIBLE : View.GONE);
            }
        });
        viewModel.refreshMail(mailId);

        FloatingActionButton fabReply = findViewById(R.id.fabReply);
        fabReply.setOnClickListener(v -> {
            if (currentMail == null) return;
            // Open compose prefilled as reply (optional)
            ComposeEmailActivityStarter.startReply(this, currentMail);
        });

        findViewById(R.id.btnDelete).setOnClickListener(v -> {
            if (currentMail == null) return;
            List<String> labels = currentMail.getLabels();
            if (labels != null && (labels.contains("bin") || labels.contains("spam"))) {
                // Permanently delete
                viewModel.deleteMail(currentMail);
                Toast.makeText(this, "Mail deleted permanently", Toast.LENGTH_SHORT).show();
                finish();
                return;
            } else {
                List<String> newLabels = new ArrayList<>();
                if (labels != null) {
                    for (String label : labels) {
                        if ("all".equals(label) || "unread".equals(label)) {
                            newLabels.add(label);
                        }
                    }
                }
                if (!newLabels.contains("bin")) {
                    newLabels.add("bin");
                }
                currentMail.setLabels(newLabels);
                viewModel.updateMail(currentMail);
                Toast.makeText(this, "Moved to Bin", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        findViewById(R.id.btnStar).setOnClickListener(v -> {
            if (currentMail == null) return;
            boolean isStarred = currentMail.getLabels() != null && currentMail.getLabels().contains("starred");
            viewModel.toggleStar(currentMail.getId(), !isStarred);
            if (isStarred) {
                currentMail.getLabels().remove("starred");
            } else {
                if (!currentMail.getLabels().contains("starred")) {
                    currentMail.getLabels().add("starred");
                }
            }
            viewModel.refreshMail(currentMail.getId());
            Toast.makeText(this, isStarred ? "Unstarred" : "Starred", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btnMarkUnread).setOnClickListener(v -> {
            if (currentMail == null) return;
            viewModel.addLabelToMail(currentMail, "unread");
            viewModel.refreshMail(currentMail.getId());
            Toast.makeText(this, "Marked as unread", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btnSpam).setOnClickListener(v -> {
            if (currentMail == null) return;
            List<String> labels = currentMail.getLabels();
            if (labels != null && labels.contains("spam")) {
                viewModel.deleteMail(currentMail);
                Toast.makeText(this, "Mail deleted permanently", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                List<String> newLabels = new ArrayList<>();
                if (labels != null) {
                    for (String label : labels) {
                        if ("all".equals(label) || "unread".equals(label)) {
                            newLabels.add(label);
                        }
                    }
                }
                if (!newLabels.contains("spam")) {
                    newLabels.add("spam");
                }
                currentMail.setLabels(newLabels);
                viewModel.updateMail(currentMail);
                viewModel.addToBlacklistFromMail(currentMail);
                Toast.makeText(this, "Marked as spam", Toast.LENGTH_SHORT).show();
                finish();
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

    // Simple helper to start Compose with prefilled data
    public static class ComposeEmailActivityStarter {
        public static void startReply(AppCompatActivity activity, Mail mail) {
            android.content.Intent intent = new android.content.Intent(activity, ComposeEmailActivity.class);
            intent.putExtra("draft_id", mail.getId());
            intent.putExtra("to", mail.getSender());
            intent.putExtra("subject", "Re: " + (mail.getSubject() == null ? "" : mail.getSubject()));
            intent.putExtra("body", "\n\n--- Original message ---\n" + (mail.getBody() == null ? "" : mail.getBody()));
            activity.startActivity(intent);
        }
    }
}
