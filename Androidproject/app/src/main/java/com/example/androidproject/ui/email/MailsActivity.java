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
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;


import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.androidproject.R;
import com.example.androidproject.data.models.Label;
import com.example.androidproject.data.models.Mail;
import com.example.androidproject.ui.BaseActivity;
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

public class MailsActivity extends BaseActivity {
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
    private String currentLabel = DEFAULT_LABEL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mail_list);

//      set up navigation drawer
        drawerLayout = findViewById(R.id.drawerLayout);
        ImageButton avatarButton = findViewById(R.id.toolbarAvatar);
        ImageButton btnMenu = findViewById(R.id.btnMenu);
        avatarButton.setOnClickListener(this::showAvatarMenu);
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
                Intent intent;
                if (mail.getLabels() != null && mail.getLabels().contains("drafts")) {
                    intent = new Intent(MailsActivity.this, EmailDetailActivity.class);
                    intent.putExtra(EmailDetailActivity.EXTRA_MAIL_ID, mail.getId());
                } else {
                    intent = new Intent(MailsActivity.this, ComposeEmailActivity.class);
                    intent.putExtra(ComposeEmailActivity.EXTRA_DRAFT_ID, mail.getId());
                }
                startActivity(intent);
            }

            @Override
            public void onStarClick(Mail mail) {
                mailsViewModel.toggleStar(mail.getId(), mail.getLabels() != null && mail.getLabels().contains("starred")).observe(
                        MailsActivity.this, resource -> {
                            if (resource != null && resource.isError()) {
                                String msg = resource.getMessage() != null ? resource.getMessage() : "Error";
                                Toast.makeText(MailsActivity.this, msg, Toast.LENGTH_SHORT).show();
                                mailsViewModel.refreshCurrentList();
                            }
                        });
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

        mailsViewModel.fetchAllMails().observe(this, st -> {
            if (st == null) return;
            switch (st.getStatus()) {
                case LOADING:
                    swipeRefreshLayout.setRefreshing(true);
                    break;
                case SUCCESS:
                    swipeRefreshLayout.setRefreshing(false);
                    break;
                case ERROR:
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(this,
                            st.getMessage() != null ? st.getMessage() : "Error",
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        });
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
        mailsViewModel.observeMailsState().observe(this, state -> {
            if (state == null) return;
            switch (state.getStatus()) {
                case LOADING:
                    swipeRefreshLayout.setRefreshing(true);
                    break;
                case SUCCESS:
                    swipeRefreshLayout.setRefreshing(false);
                    break;
                case ERROR:
                    swipeRefreshLayout.setRefreshing(false);
                    int code = state.getErrorCode();
                    Toast.makeText(this,
                            state.getMessage() != null ? state.getMessage() : "Error",
                            Toast.LENGTH_SHORT).show();

                    break;
            }
        });

//      set up swipe to refresh
        swipeRefreshLayout.setOnRefreshListener(() -> mailsViewModel.refreshCurrentList());


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

                mailsViewModel.setSelectedLabel(name);

                drawerAdapter.setSelectedLabel(name);
                drawerLayout.closeDrawer(GravityCompat.START);
                currentLabel = name;
            }

            @Override
            public void onCreateLabelClick() {
                new AddLabelBottomSheet().show(getSupportFragmentManager(), "add_label");
            }

            @Override
            public void onManageLabelsClick() {
                if (isUserLabel(currentLabel)) {
                    EditLabelBottomSheet.newInstance(currentLabel).show(getSupportFragmentManager(), "edit_label");
                } else {
                    Toast.makeText(MailsActivity.this, R.string.error_system_label, Toast.LENGTH_SHORT).show();
                }
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
        mailsViewModel.setSelectedLabel(DEFAULT_LABEL);
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
                if (query.isEmpty()) {
                    tvCurrentLabel.setText(currentLabel);
                } else {
                    tvCurrentLabel.setText(("Search: " + query).substring(0, Math.min(30, query.length() + 8)) + (query.length() > 30 ? "..." : ""));
                }
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

    public void onResume() {
        super.onResume();
        Log.d("MailsActivity", "onResume");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("MailsActivity", "onStart");
        mailsViewModel.refreshCurrentList();
        labelsViewModel.refreshLabels();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("MailsActivity", "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("MailsActivity", "onDestroy");
    }

    // Helper Methods
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

    private boolean isUserLabel(String labelName) {
        if (labelName == null) return false;
        String lower = labelName.toLowerCase();
        List<String> systemLabels = Arrays.asList(
                "inbox", "sent", "starred", "spam", "bin", "drafts", "all"
        );
        return !systemLabels.contains(lower);
    }
}
