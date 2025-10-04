package com.example.androidproject.ui.email;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.androidproject.R;
import com.example.androidproject.data.models.Label;
import com.example.androidproject.data.models.Mail;
import com.example.androidproject.ui.adapters.DrawerAdapter;
import com.example.androidproject.ui.adapters.DrawerItem;
import com.example.androidproject.ui.adapters.MailsListAdapter;
import com.example.androidproject.ui.auth.LoginActivity;
import com.example.androidproject.ui.label.AddLabelBottomSheet;
import com.example.androidproject.ui.label.EditLabelBottomSheet;
import com.example.androidproject.viewModel.LabelsViewModel;
import com.example.androidproject.viewModel.MailsViewModel;
import com.example.androidproject.viewModel.UserViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MailsActivity extends AppCompatActivity {
    private MailsViewModel mailsViewModel;
    private LabelsViewModel labelsViewModel;
    private UserViewModel userViewModel;

    private MailsListAdapter mailsListAdapter;

    private DrawerLayout drawerLayout;
    private RecyclerView drawerRecycler;
    private DrawerAdapter drawerAdapter;

    private EditText searchInputText;
    private TextView tvCurrentLabel;

    private static final String DEFAULT_LABEL = "inbox";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mail_list);

//      set up navigation drawer
        drawerLayout = findViewById(R.id.drawerLayout);
        ImageButton avatarButton = findViewById(R.id.toolbarAvatar);
        ImageButton btnMenu = findViewById(R.id.btnMenu);
        avatarButton.setOnClickListener(v -> showAvatarMenu(v));
        btnMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        ImageButton btnCompose = findViewById(R.id.btnCompose);

        tvCurrentLabel = findViewById(R.id.tvCurrentLabel);
        tvCurrentLabel.setText(DEFAULT_LABEL);

//      set up mails emails list
        RecyclerView mailsRecycler = findViewById(R.id.mailsRecycler);
        mailsRecycler.setLayoutManager(new LinearLayoutManager(this));
        mailsListAdapter = new MailsListAdapter(new ArrayList<>(), new MailsListAdapter.OnMailsListAdapterListener() {
            @Override
            public void onMailClick(Mail mail) {
                if (mail.getLabels() != null && mail.getLabels().contains("starred")) {
                    mailsViewModel.removeLabelFromMail(mail, "starred");
                } else {
                    mailsViewModel.addLabelToMail(mail, "starred");
                }
            }

            @Override
            public void onStarClick(Mail mail) {
                if (mail.getLabels() != null && mail.getLabels().contains("starred")) {
                    mailsViewModel.removeLabelFromMail(mail, "starred");
                } else mailsViewModel.addLabelToMail(mail, "starred");
            }
        });
        mailsListAdapter.setHasStableIds(true);
        mailsRecycler.setAdapter(mailsListAdapter);

        searchInputText = findViewById(R.id.editTextSearch);

        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipeRefreshMails);

//      set up view models
        mailsViewModel = new ViewModelProvider(this).get(MailsViewModel.class);
        labelsViewModel = new ViewModelProvider(this).get(LabelsViewModel.class);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        mailsViewModel.observeMailList().observe(this, mailsList -> {
            mailsListAdapter.setMails(mailsList);
            // stop refreshing animation when data is loaded
            if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
        });
        userViewModel.getUser().observe(this, user -> {
            if (user != null && user.getPicture() != null) {
                Uri uri = Uri.parse(user.getPicture());
                avatarButton.setImageURI(uri);
            } else {
                avatarButton.setImageResource(R.drawable.search_bar_bg);
            }
        });

//      set up swipe to refresh
        swipeRefreshLayout.setOnRefreshListener(() -> mailsViewModel.loadMails());


//      set up compose email button
        btnCompose.setOnClickListener(v ->
                startActivity((new Intent(
                        MailsActivity.this, ComposeEmailActivity.class))));


        drawerRecycler = findViewById(R.id.drawerRecycler);
        drawerRecycler.setLayoutManager(new LinearLayoutManager(this));

//      set up navigation drawer

        drawerAdapter = new DrawerAdapter(new DrawerAdapter.OnItemClickListener() {
            @Override
            public void onLabelClick(DrawerItem.LabelItem labelItem) {
                String name = labelItem.label != null ? labelItem.label.getName() : DEFAULT_LABEL;
                tvCurrentLabel.setText(name);

                mailsViewModel.getMailsByLabel(name);

                drawerAdapter.setSelectedLabel(name);
                drawerLayout.closeDrawer(GravityCompat.START);
            }

            @Override
            public void onCreateLabelClick() {
                startActivity(new Intent(MailsActivity.this, AddLabelBottomSheet.class));
            }

            @Override
            public void onManageLabelsClick() {
                startActivity(new Intent(MailsActivity.this, EditLabelBottomSheet.class));
            }
        });
        drawerRecycler.setAdapter(drawerAdapter);


//      create static items for drawer
        DrawerItem.HeaderItem headerItem = new DrawerItem.HeaderItem();
        DrawerItem.SectionItem sectionItem = new DrawerItem.SectionItem();

        List<DrawerItem.LabelItem> systemLabels = buildSystemLabels();

//      set static labels and create new List for user labels
        drawerAdapter.submitList(
                DrawerItem.buildDrawerItems(headerItem, systemLabels, sectionItem, new ArrayList<>())
        );
        mailsViewModel.getMailsByLabel(DEFAULT_LABEL);
        drawerAdapter.setSelectedLabel(DEFAULT_LABEL);

//      observe user labels and update drawer when they change
        labelsViewModel.getLabels().observe(this, userLabels -> {
            Log.d("LabelsVM", "observe: size=" + (userLabels == null ? -1 : userLabels.size()));
            List<DrawerItem.LabelItem> userLabelItems = buildUserLabels(userLabels);

            List<DrawerItem> items = DrawerItem.buildDrawerItems(headerItem, systemLabels, sectionItem, userLabelItems);
            items.add(new DrawerItem.ActionItem(
                    DrawerItem.ActionItem.Action.CREATE,
                    R.drawable.ic_add,
                    getString(R.string.create_new_label)
            ));
            items.add(new DrawerItem.ActionItem(
                    DrawerItem.ActionItem.Action.MANAGE,
                    R.drawable.ic_edit,
                    getString(R.string.manage_labels)
            ));
            drawerAdapter.submitList(items);
        });


//      set up search button
        searchInputText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                String query = searchInputText.getText().toString();
                mailsViewModel.searchMails(query);
                // hide keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchInputText.getWindowToken(), 0);
                return true; // event handled
            }
            return false;
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


    private List<DrawerItem.LabelItem> buildSystemLabels() {
        return Arrays.asList(
                new DrawerItem.LabelItem(new Label("inbox")),
                new DrawerItem.LabelItem(new Label("starred")),
                new DrawerItem.LabelItem(new Label("sent")),
                new DrawerItem.LabelItem(new Label("drafts")),
                new DrawerItem.LabelItem(new Label("spam")),
                new DrawerItem.LabelItem(new Label("bin")),
                new DrawerItem.LabelItem(new Label("all"))
        );
    }

    private List<DrawerItem.LabelItem> buildUserLabels(List<Label> userLabels) {
        List<DrawerItem.LabelItem> items = new ArrayList<>();
        if (userLabels != null) {
            for (Label label : userLabels) {
                items.add(new DrawerItem.LabelItem(label));
            }
        }
        return items;
    }
    private void showAvatarMenu(View anchor) {
        PopupMenu popup = new PopupMenu(this, anchor);
        popup.getMenuInflater().inflate(R.menu.user_settings, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_logout) {
                handleLogout();
                return true;
            }
            return false;
        });
        popup.show();
    }
    private void handleLogout() {
        userViewModel.logout();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
