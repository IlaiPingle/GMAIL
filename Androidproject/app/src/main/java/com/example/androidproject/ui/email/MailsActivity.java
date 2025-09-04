package com.example.androidproject.ui.email;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidproject.R;
import com.example.androidproject.data.models.Mail;
import com.example.androidproject.ui.adapters.MailsListAdapter;
import com.example.androidproject.viewModel.MailsViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class MailsActivity extends AppCompatActivity {
    private MailsViewModel mailsViewModel;
    private MailsListAdapter mailsListAdapter;
    private MaterialToolbar toolbar;
    private DrawerLayout drawerLayout;
    private RecyclerView mailsRecycler;
    private NavigationView navLabels;
    private TextInputEditText searchInputText;
    private FloatingActionButton fabCompose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mail_list);

        toolbar = findViewById(R.id.topAppBar);
        drawerLayout = findViewById(R.id.drawerLayout);
        mailsRecycler = findViewById(R.id.mailsRecycler);

        mailsRecycler.setLayoutManager(new LinearLayoutManager(this));

        searchInputText = findViewById(R.id.searchEditText);
        navLabels = findViewById(R.id.navLabels);
        fabCompose = findViewById(R.id.fabCompose);

        // open/close drawer on menu icon click
        toolbar.setNavigationOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        navLabels.post(() -> {
            int screenW = getResources().getDisplayMetrics().widthPixels;
            navLabels.getLayoutParams().width = (int) (screenW * 0.75f);
            navLabels.requestLayout();
        });

        navLabels.setItemIconTintList(null);

        mailsViewModel = new ViewModelProvider(this).get(MailsViewModel.class);

        mailsListAdapter = new MailsListAdapter(new ArrayList<>(), new MailsListAdapter.OnMailsListAdapterListener() {
            @Override
            public void onMailClick(Mail mail) {
                Intent intent = new Intent(MailsActivity.this, ComposeEmailActivity.class);
                intent.putExtra("mail_id", mail.getId());
                startActivity(intent);
            }

            @Override
            public void onStarClick(Mail mail) {
                mailsViewModel.addLabelToMail(mail, "starred");
            }
        });
        mailsRecycler.setAdapter(mailsListAdapter);

        mailsViewModel.getMails().observe(this, mailsList -> {
            mailsListAdapter.setMails(mailsList);
        });

        navLabels.setNavigationItemSelectedListener(item -> {
            String labelId = menuItemIdToLabel(item.getItemId());
            if ("all".equals(labelId)) {
                mailsViewModel.getMails();
            } else {
                mailsViewModel.getMailsByLabel(labelId);
            }
            item.setChecked(true);
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        fabCompose.setOnClickListener(v -> {
            Intent intent = new Intent(this, ComposeEmailActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("MailsActivity", "onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("MailsActivity", "onDestroy");
    }

    private static String menuItemIdToLabel(int menuId) {
        if (menuId == R.id.nav_inbox) return "inbox";
        if (menuId == R.id.nav_starred) return "starred";
        if (menuId == R.id.nav_sent) return "sent";
        if (menuId == R.id.nav_all_mails) return "all";
        return "inbox";
    }
}
