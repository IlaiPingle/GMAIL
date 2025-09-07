package com.example.androidproject.ui.email;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.LiveData;
import com.example.androidproject.R;
import com.example.androidproject.model.EmailItem;
import com.example.androidproject.ui.auth.LoginActivity;
import com.example.androidproject.data.models.User;
import com.example.androidproject.data.models.Mail;
import com.example.androidproject.viewModel.InboxViewModel;
import com.example.androidproject.viewModel.MailsViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.List;

/**
 * InboxActivity displays a list of emails with functionalities like search, refresh, and compose.
 */
public class InboxActivity extends AppCompatActivity implements
        EmailAdapter.OnEmailClickListener,
        NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private RecyclerView recyclerView;
    private EmailAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private final List<EmailItem> emailList = new ArrayList<>();
    private final List<Mail> currentMails = new ArrayList<>();
    private TabLayout tabLayout;
    private boolean isLoading = false;
    private String currentLabel = "inbox";
    private InboxViewModel inboxViewModel;
    private MailsViewModel mailsViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Setup navigation drawer
        drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Back gesture handling (closes drawer first)
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });

        // Setup tabs
        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Primary"));
        tabLayout.addTab(tabLayout.newTab().setText("Social"));
        tabLayout.addTab(tabLayout.newTab().setText("Promotions"));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String category;
                switch (tab.getPosition()) {
                    case 1: category = "social"; break;
                    case 2: category = "promotions"; break;
                    default: category = "primary"; break;
                }
                filterEmailsByCategory(category);
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        // Setup RecyclerView
        recyclerView = findViewById(R.id.recyclerViewEmails);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Setup adapter
        adapter = new EmailAdapter(emailList, this);
        recyclerView.setAdapter(adapter);

        // Setup ViewModels
        inboxViewModel = new ViewModelProvider(this).get(InboxViewModel.class);
        mailsViewModel = new ViewModelProvider(this).get(MailsViewModel.class);
        observeAndRender(mailsViewModel.getMails());
        inboxViewModel.getLoggedOut().observe(this, loggedOut -> {
            if (loggedOut != null && loggedOut) {
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
        inboxViewModel.getError().observe(this, errorMsg -> {
            if (errorMsg != null) {
                Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
            }
        });
        inboxViewModel.getLoading().observe(this, isLoading -> {
            // Optionally show a loading indicator
        });
        inboxViewModel.getCurrentUser().observe(this, this::bindToolbarAvatar);

        // Setup SwipeRefreshLayout
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeRefreshLayout.setOnRefreshListener(this::refreshEmails);

        // Initial load
        loadEmails();

        // Setup swipe actions
        setupSwipeActions();


        // Setup FAB
        FloatingActionButton fab = findViewById(R.id.fabCompose);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(this, ComposeEmailActivity.class);
            startActivity(intent);
        });

        // Setup SearchView
        EditText editTextSearch = findViewById(R.id.editTextSearch);
        ImageButton buttonSearch = findViewById(R.id.btnSearch);
        buttonSearch.setOnClickListener(v -> {
            String query = editTextSearch.getText().toString().trim();
            performSearch(query);
        });
        editTextSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                String query = editTextSearch.getText().toString().trim();
                performSearch(query);
                return true;
            }
            return false;
        });
        ImageButton btnClear = findViewById(R.id.btnClear);
        btnClear.setOnClickListener(v -> editTextSearch.setText(""));
        editTextSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                btnClear.setVisibility(editable.length() > 0 ? View.VISIBLE : View.GONE);
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
    }

    /**
     * Observe LiveData and render emails in RecyclerView.
     * @param liveData The LiveData<List<Mail>> to observe.
     */
    private void observeAndRender(LiveData<List<Mail>> liveData) {
        finishLoading();
        liveData.observe(this, mails -> {
            finishLoading();
            if (mails == null) return;
            currentMails.clear();
            currentMails.addAll(mails);
            emailList.clear();
            for (Mail mail : mails) {
                emailList.add(mapToItem(mail));
            }
            adapter.setItems(emailList);
            if (emailList.isEmpty()) {
                showInfo("No emails yet");
            }
        });
    }

    /**
     * Map Mail model to EmailItem for RecyclerView display.
     * @param m The Mail object to map.
     * @return The corresponding EmailItem.
     */
    private EmailItem mapToItem(Mail m) {
        String preview = m.getBody() == null ? "" : m.getBody();
        String time = "";
        boolean isRead = m.getLabels() == null || !m.getLabels().contains("unread");
        EmailItem item = new EmailItem(
                m.getSender() == null ? "" : m.getSender(),
                m.getSubject() == null ? "" : m.getSubject(),
                preview,
                time,
                isRead
        );
        item.setStarred(m.getLabels() != null && m.getLabels().contains("starred"));
        return item;
    }

    /**
     * Setup swipe actions for RecyclerView items (delete on swipe).
     */
    private void setupSwipeActions() {
        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                if (position < 0 || position >= emailList.size()) {
                    adapter.notifyItemChanged(position);
                    return;
                }
                Mail mail = currentMails.get(position);

                currentMails.remove(position);
                if (position < emailList.size()) {
                    emailList.remove(position);
                    adapter.setItems(emailList);
                }

                if ("bin".equalsIgnoreCase(currentLabel) || "spam".equalsIgnoreCase(currentLabel)) {
                    mailsViewModel.deleteMailById(mail.getId());
                } else {
                    if (!TextUtils.isEmpty(currentLabel) && "inbox".equalsIgnoreCase(currentLabel)) {
                        mailsViewModel.removeLabelFromMail(mail, currentLabel);
                    }
                    mailsViewModel.addLabelToMail(mail, "bin");
                }
                Snackbar.make(recyclerView, "Email moved to bin", Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView rv,
                                    @NonNull RecyclerView.ViewHolder vh,
                                    float dX, float dY, int actionState, boolean isCurrentlyActive) {
                View itemView = vh.itemView;
                Paint paint = new Paint();
                if (dX > 0) {
                    paint.setColor(Color.parseColor("#C62828")); // red for delete
                    c.drawRect(itemView.getLeft(), itemView.getTop(),
                            itemView.getLeft() + dX, itemView.getBottom(), paint);
                } else if (dX < 0) {
                    paint.setColor(Color.parseColor("#C62828")); // red for delete
                    c.drawRect(itemView.getRight() + dX, itemView.getTop(),
                            itemView.getRight(), itemView.getBottom(), paint);
                }
                super.onChildDraw(c, rv, vh, dX, dY, actionState, isCurrentlyActive);
            }
        };
        new ItemTouchHelper(callback).attachToRecyclerView(recyclerView);
    }

    /**
     * Handle navigation item selection.
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        // Handle navigation item clicks
        if (id == R.id.nav_all_inboxes) {
            currentLabel = "inbox";
            loadEmails();
        } else if (id == R.id.nav_inbox) {
            currentLabel = "inbox";
            loadEmails();
        } else if (id == R.id.nav_starred) {
            filterEmailsByLabel("starred");
        } else if (id == R.id.nav_snoozed) {
            filterEmailsByLabel("snoozed");
        } else if (id == R.id.nav_sent) {
            currentLabel = "sent";
            loadEmails();
        } else if (id == R.id.nav_logout) {
            performLogout();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Filter emails by category (Primary, Social, Promotions).
     */
    private void filterEmailsByCategory(String category) {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(true);
        }
        observeAndRender(mailsViewModel.getMailsByLabel(category));
    }

    /**
     * Filter emails by label (starred, snoozed, etc.).
     */
    private void filterEmailsByLabel(String label) {
        currentLabel = label;
        observeAndRender(mailsViewModel.getMailsByLabel(label));
    }

    /**
     * Load emails from the API and display them.
     */
    private void loadEmails() {
        if (isLoading) return;
        isLoading = true;
        // Show loading indicator
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setEnabled(false);
            swipeRefreshLayout.post(() -> swipeRefreshLayout.setRefreshing(true));
        }
        observeAndRender(mailsViewModel.getMails());
    }

    /**
     * Finish loading state and hide indicators.
     */
    private void finishLoading() {
        isLoading = false;
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
            swipeRefreshLayout.setEnabled(true);
        }
    }

    /**
     * Show an informational "Snackbar" message.
     */
    private void showInfo(String message) {
        Snackbar.make(recyclerView, message, Snackbar.LENGTH_SHORT).show();
    }

    /**
     * Refresh emails based on the current label/category.
     */
    private void refreshEmails() {
        if ("inbox".equals(currentLabel)) {
            observeAndRender(mailsViewModel.getMails());
        } else {
            observeAndRender(mailsViewModel.getMailsByLabel(currentLabel));
        }
    }

    /**
     * Handle email item click to open detail view.
     */
    @Override
    public void onEmailClick(int position) {
        if (position < 0 || position >= currentMails.size()) return;
        String id = currentMails.get(position).getId();
        Intent intent = new Intent(this, EmailDetailActivity.class);
        intent.putExtra(EmailDetailActivity.EXTRA_MAIL_ID, id);
        startActivity(intent);
    }

    /**
     * Perform logout action.
     */
    private void performLogout() {
        inboxViewModel.logout();
    }

    /**
     * Bind user avatar in the toolbar.
     */
    private void bindToolbarAvatar(User user) {
        ImageView avatarImg = findViewById(R.id.toolbarAvatar);
        TextView avatarInitial = findViewById(R.id.toolbarAvatarInitial);
        if (avatarImg == null || avatarInitial == null) return;

        String first = user == null ? "" : user.firstName;
        String sur   = user == null ? "" : user.surName;
        String username  = user == null ? "" : user.username;
        String pic   = user == null ? "" : (user.picture == null ? "" : user.picture);

        String initials = "";
        if (!TextUtils.isEmpty(first)) initials += first.substring(0, 1).toUpperCase();
        if (!TextUtils.isEmpty(sur))   initials += sur.substring(0, 1).toUpperCase();
        if (TextUtils.isEmpty(initials) && !TextUtils.isEmpty(username)) {
            initials = username.substring(0, 1).toUpperCase();
        }
        if (TextUtils.isEmpty(initials)) initials = "?";
        avatarInitial.setText(initials);

        if (!TextUtils.isEmpty(pic)) {
            avatarInitial.setVisibility(View.GONE);
            avatarImg.setVisibility(View.VISIBLE);
            try {
                avatarImg.setImageURI(Uri.parse(pic));
            } catch (Exception ignored) {}
        } else {
            avatarImg.setVisibility(View.GONE);
            avatarInitial.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Perform search with the given query string.
     * @param query The search query string.
     */
    private void performSearch(String query) {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(true);
        }
        observeAndRender(mailsViewModel.searchMails(query));
    }
}