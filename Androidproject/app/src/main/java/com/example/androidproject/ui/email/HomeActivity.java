package com.example.androidproject.ui.email;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.androidproject.R;
import com.example.androidproject.api.EmailApiService;
import com.example.androidproject.model.EmailItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.List;

import com.example.androidproject.model.EmailData;
import com.example.androidproject.api.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * HomeActivity displays a list of emails with functionalities like search, refresh, and compose.
 */
public class HomeActivity extends AppCompatActivity implements
        EmailAdapter.OnEmailClickListener,
        NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private RecyclerView recyclerView;
    private EmailAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private final List<EmailItem> emailList = new ArrayList<>();
    private TabLayout tabLayout;
    private boolean isLoading = false;
    private String currentLabel = "inbox";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

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

        // Setup SwipeRefreshLayout
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeRefreshLayout.setOnRefreshListener(this::refreshEmails);

        // Load emails from API
        fetchEmailsFromApi();

        // Setup swipe actions
        setupSwipeActions();


        // Setup FAB
        FloatingActionButton fab = findViewById(R.id.fabCompose);
        fab.setOnClickListener(v -> {
            Toast.makeText(this, "Compose new email", Toast.LENGTH_SHORT).show();
            // Launch compose activity
        });

        // Setup SearchView
        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    /**
     * Setup swipe actions for RecyclerView items (archive and delete).
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
                EmailItem deletedEmail = emailList.get(position);

                emailList.remove(position);
                adapter.notifyItemRemoved(position);

                String action = (direction == ItemTouchHelper.RIGHT) ? "archived" : "deleted";

                Snackbar.make(recyclerView, "Email " + action, Snackbar.LENGTH_LONG)
                        .setAction("UNDO", v -> {
                            emailList.add(position, deletedEmail);
                            adapter.notifyItemInserted(position);
                        }).show();
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView rv,
                                    @NonNull RecyclerView.ViewHolder vh,
                                    float dX, float dY, int actionState, boolean isCurrentlyActive) {
                View itemView = vh.itemView;
                Paint paint = new Paint();
                if (dX > 0) {
                    paint.setColor(Color.parseColor("#2E7D32")); // green for archive
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
            fetchEmailsFromApi();
        } else if (id == R.id.nav_inbox) {
            currentLabel = "inbox";
            fetchEmailsFromApi();
        } else if (id == R.id.nav_starred) {
            filterEmailsByLabel("starred");
        } else if (id == R.id.nav_snoozed) {
            filterEmailsByLabel("snoozed");
        } else if (id == R.id.nav_sent) {
            currentLabel = "sent";
            fetchEmailsFromApi();
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
        EmailApiService apiService = ApiClient.getClient().create(EmailApiService.class);
        Call<List<EmailData>> call = apiService.getMails(category);
        call.enqueue(new Callback<List<EmailData>>() {
            @Override
            public void onResponse(Call<List<EmailData>> call, Response<List<EmailData>> response) {
                finishLoading();
                if (response.isSuccessful() && response.body() != null) {
                    List<EmailItem> items = new ArrayList<>();
                    for (EmailData email : response.body()) {
                        items.add(email.toEmailItem());
                    }
                    // Update UI
                    adapter.setItems(items);
                    if (items.isEmpty()) {
                        showInfo("No emails yet");
                    }
                } else {
                    String serverMsg = "";
                    try {
                        if (response.errorBody() != null) {
                            serverMsg = response.errorBody().string();
                        }
                    } catch (Exception ignored) {}
                    if (response.code() == 401) {
                        showErrorWithRetry("Session expired. Please log in again.");
                        // Here you would redirect to login activity
                        return;
                    }
                    String msg = serverMsg.isEmpty()
                            ? "Failed to load emails (" + response.code() + ")"
                            : "Failed to load emails: " + serverMsg;
                    showErrorWithRetry(msg);
                }
            }

            @Override
            public void onFailure(Call<List<EmailData>> call, Throwable t) {
                finishLoading();
                showErrorWithRetry("Network error: " + t.getMessage());
            }
        });
    }

    /**
     * Filter emails by label (starred, snoozed).
     */
    private void filterEmailsByLabel(String label) {
        currentLabel = label;
        if (!emailList.isEmpty()) {
            List<EmailItem> filtered = new ArrayList<>();
            if (label.equals("starred")) {
                for (EmailItem e : emailList) {
                    if (e.isStarred()) filtered.add(e);
                }
                adapter.setItems(filtered);
                return;
            }
        }
        EmailApiService apiService = ApiClient.getClient().create(EmailApiService.class);
        Call<List<EmailData>> call = apiService.getMailsByLabel(label);
        call.enqueue(new Callback<List<EmailData>>() {
            @Override
            public void onResponse(Call<List<EmailData>> call, Response<List<EmailData>> response) {
                finishLoading();
                if (response.isSuccessful() && response.body() != null) {
                    List<EmailItem> items = new ArrayList<>();
                    for (EmailData email : response.body()) {
                        items.add(email.toEmailItem());
                    }
                    // Update UI
                    adapter.setItems(items);
                    if (items.isEmpty()) {
                        showInfo("No emails yet");
                    }
                } else {
                    String serverMsg = "";
                    try {
                        if (response.errorBody() != null) {
                            serverMsg = response.errorBody().string();
                        }
                    } catch (Exception ignored) {}
                    if (response.code() == 401) {
                        showErrorWithRetry("Session expired. Please log in again.");
                        // Here you would redirect to login activity
                        return;
                    }
                    String msg = serverMsg.isEmpty()
                            ? "Failed to load emails (" + response.code() + ")"
                            : "Failed to load emails: " + serverMsg;
                    showErrorWithRetry(msg);
                }
            }

            @Override
            public void onFailure(Call<List<EmailData>> call, Throwable t) {
                finishLoading();
                showErrorWithRetry("Network error: " + t.getMessage());
            }
        });
    }

    /**
     * Fetch emails from the API and update the RecyclerView.
     */
    private void fetchEmailsFromApi() {
        if (isLoading) return;
        isLoading = true;
        // Show loading indicator
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setEnabled(false);
            swipeRefreshLayout.post(() -> swipeRefreshLayout.setRefreshing(true));
        }

        EmailApiService apiService = ApiClient.getClient().create(EmailApiService.class);
        Call<List<EmailData>> call = apiService.getMails();
        call.enqueue(new Callback<List<EmailData>>() {
            @Override
            public void onResponse(Call<List<EmailData>> call, Response<List<EmailData>> response) {
                finishLoading();
                if (response.isSuccessful() && response.body() != null) {
                    List<EmailItem> items = new ArrayList<>();
                    for (EmailData email : response.body()) {
                        items.add(email.toEmailItem());
                    }
                    // Update UI
                    adapter.setItems(items);
                    if (items.isEmpty()) {
                        showInfo("No emails yet");
                    }
                } else {
                    String serverMsg = "";
                    try {
                        if (response.errorBody() != null) {
                            serverMsg = response.errorBody().string();
                        }
                    } catch (Exception ignored) {}
                    if (response.code() == 401) {
                        showErrorWithRetry("Session expired. Please log in again.");
                        // Here you would redirect to login activity
                        return;
                    }
                    String msg = serverMsg.isEmpty()
                            ? "Failed to load emails (" + response.code() + ")"
                            : "Failed to load emails: " + serverMsg;
                    showErrorWithRetry(msg);
                }
            }

            @Override
            public void onFailure(Call<List<EmailData>> call, Throwable t) {
                finishLoading();
                showErrorWithRetry("Network error: " + t.getMessage());
            }
        });
    }

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
    private void showErrorWithRetry(String message) {
        Snackbar.make(recyclerView, message, Snackbar.LENGTH_INDEFINITE)
                .setAction("RETRY", v -> fetchEmailsFromApi())
                .show();
    }

    /**
     * Show an informational "Snackbar" message.
     */
    private void showInfo(String message) {
        Snackbar.make(recyclerView, message, Snackbar.LENGTH_SHORT).show();
    }

    /**
     * When refresh is triggered, fetch emails from the API.
     */
    private void refreshEmails() {
        fetchEmailsFromApi();
    }

    /**
     * Handle email item click to mark as read and open detail view.
     */
    @Override
    public void onEmailClick(int position) {
        EmailItem email = adapter.getItem(position);
        email.setRead(true);
        adapter.notifyItemChanged(position);

        // Here you would launch the email detail activity
        Toast.makeText(this, "Email from: " + email.getSender(), Toast.LENGTH_SHORT).show();
    }
}