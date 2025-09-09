package com.example.androidproject.ui.email;

import android.os.Bundle;
import android.view.Menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;

import com.example.androidproject.R;
import com.example.androidproject.viewModel.LabelsViewModel;
import com.google.android.material.navigation.NavigationView;

/**
 * HomeActivity displays a list of emails with functionalities like search, refresh, and compose.
 */
public class HomeActivity extends AppCompatActivity {

    private NavigationView navBar;
    private LabelsViewModel labelsViewModel;
    private DrawerLayout drawerLayout;

//    private SharedMailState sharedMailState;


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

        drawerLayout = findViewById(R.id.drawerLayout);
        navBar = findViewById(R.id.navigationView);

//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_open, R.string.nav_close);
//        drawerLayout.addDrawerListener(toggle);
//        toggle.syncState();

        labelsViewModel = new ViewModelProvider(this).get(LabelsViewModel.class);

//        sharedMailState = new ViewModelProvider(this).get(SharedMailStateViewModel.class);


        Menu menu = navBar.getMenu();
        menu.clear();
        labelsViewModel.getLabels().observe(this, labels -> {
            menu.clear();
            menu.add(0, 1, Menu.NONE, "All mail").setIcon(R.drawable.ic_inbox);
            if (labels != null) {
                int baseId = 1000;
                for (int i = 0; i < labels.size(); i++) {
                    String name = labels.get(i).getLabelName();
//                    menu.add(0, baseId + i, Menu.NONE, name).setIcon(R.drawable.ic_label);
                }
            }
        });
        navBar.setNavigationItemSelectedListener(item -> {
            drawerLayout.closeDrawers();
            if (item.getItemId() == 1) {
//                sharedMailState.setSelectedLabel(null); // כל המיילים
            } else {
//                sharedMailState.setSelectedLabel(item.getTitle().toString());
            }
            return true;
        });

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.content, new MailsFragment())
                    .commit();
        }
    }
//    @Override
////    public void onBackPressed() {
//        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
//            drawerLayout.closeDrawer(GravityCompat.START);
//        } else super.onBackPressed();
//    }
}

//    // Back gesture handling (closes drawer first)
//    getOnBackPressedDispatcher().
//
//    addCallback(this,new OnBackPressedCallback(true) {
//        @Override
//        public void handleOnBackPressed () {
//            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
//                drawerLayout.closeDrawer(GravityCompat.START);
//            } else {
//                setEnabled(false);
//                getOnBackPressedDispatcher().onBackPressed();
//            }
//        }
//    });
//
//
//
//
//// Setup SwipeRefreshLayout
//    swipeRefreshLayout =findViewById(R.id.swipeRefreshLayout);
//    swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
//    android.R.color.holo_green_light,
//    android.R.color.holo_orange_light,
//    android.R.color.holo_red_light);
//    swipeRefreshLayout.setOnRefreshListener(this::refreshEmails);
//
//
//
//    // Setup FAB
//    FloatingActionButton fab = findViewById(R.id.fabCompose);
//        fab.
//
//                setOnClickListener(v ->
//
//    {
//        Intent intent = new Intent(this, ComposeEmailActivity.class);
//
//        startActivity(intent);
//    });
//
//    // Setup SearchView
//    EditText editTextSearch = findViewById(R.id.editTextSearch);
//    ImageButton buttonSearch = findViewById(R.id.btnSearch);
//        buttonSearch.
//
//                setOnClickListener(v ->
//
//    {
//        String query = editTextSearch.getText().toString().trim();
//
//        performSearch(query);
//    });
//        editTextSearch.
//
//                setOnEditorActionListener((v,actionId,event)->
//
//    {
//        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
//                (event != null && event.
//
//                        getKeyCode() == KeyEvent.KEYCODE_ENTER && event.
//
//                        getAction() == KeyEvent.ACTION_DOWN)) {
//            String query = editTextSearch.getText().toString().trim();
//
//            performSearch(query);
//            return true;
//        }
//        return false;
//    });
//}
//
///**
// * Setup swipe actions for RecyclerView items (delete on swipe).
// */
//private void setupSwipeActions() {
//    ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0,
//            ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
//
//        @Override
//        public boolean onMove(@NonNull RecyclerView recyclerView,
//                              @NonNull RecyclerView.ViewHolder viewHolder,
//                              @NonNull RecyclerView.ViewHolder target) {
//            return false;
//        }
//
//        @Override
//        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
//            int position = viewHolder.getAdapterPosition();
//            EmailItem deletedEmail = adapter.getItem(position);
//            EmailData deleteData = emailMap.get(deletedEmail);
//            if (deletedEmail == null || deleteData == null) {
//                adapter.notifyItemChanged(position);
//                return;
//            }
//
//            emailList.remove(deletedEmail);
//            emailDataList.remove(deleteData);
//            emailMap.remove(deletedEmail);
//            adapter.setItems(emailList);
//
//            EmailApiService api = ApiClient.getClient().create(EmailApiService.class);
//            api.deleteMail(deleteData.getId()).enqueue(new Callback<Void>() {
//                @Override
//                public void onResponse(Call<Void> call, Response<Void> response) {
//                    if (!response.isSuccessful()) {
//                        emailList.add(position, deletedEmail);
//                        emailDataList.add(position, deleteData);
//                        emailMap.put(deletedEmail, deleteData);
//                        adapter.setItems(emailList);
//                        Snackbar.make(recyclerView, "Failed to delete email (" + response.code() + ")", Snackbar.LENGTH_LONG).show();
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<Void> call, Throwable t) {
//                    emailList.add(position, deletedEmail);
//                    emailDataList.add(position, deleteData);
//                    emailMap.put(deletedEmail, deleteData);
//                    adapter.setItems(emailList);
//                    Snackbar.make(recyclerView, "Network error: " + t.getMessage(), Snackbar.LENGTH_LONG).show();
//
//                }
//            });
//            Snackbar.make(recyclerView, "Email deleted", Snackbar.LENGTH_LONG).show();
//        }
//
//        @Override
//        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView rv,
//                                @NonNull RecyclerView.ViewHolder vh,
//                                float dX, float dY, int actionState, boolean isCurrentlyActive) {
//            View itemView = vh.itemView;
//            Paint paint = new Paint();
//            if (dX > 0) {
//                paint.setColor(Color.parseColor("#C62828")); // red for delete
//                c.drawRect(itemView.getLeft(), itemView.getTop(),
//                        itemView.getLeft() + dX, itemView.getBottom(), paint);
//            } else if (dX < 0) {
//                paint.setColor(Color.parseColor("#C62828")); // red for delete
//                c.drawRect(itemView.getRight() + dX, itemView.getTop(),
//                        itemView.getRight(), itemView.getBottom(), paint);
//            }
//            super.onChildDraw(c, rv, vh, dX, dY, actionState, isCurrentlyActive);
//        }
//    };
//    new ItemTouchHelper(callback).attachToRecyclerView(recyclerView);
//}
//
///**
// * Handle navigation item selection.
// */
//@Override
//public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//    int id = item.getItemId();
//
//    // Handle navigation item clicks
//    if (id == R.id.nav_all_inboxes) {
//        currentLabel = "inbox";
//        fetchEmailsFromApi();
//    } else if (id == R.id.nav_inbox) {
//        currentLabel = "inbox";
//        fetchEmailsFromApi();
//    } else if (id == R.id.nav_starred) {
//        filterEmailsByLabel("starred");
//    } else if (id == R.id.nav_snoozed) {
//        filterEmailsByLabel("snoozed");
//    } else if (id == R.id.nav_sent) {
//        currentLabel = "sent";
//        fetchEmailsFromApi();
//    } else if (id == R.id.nav_logout) {
//        performLogout();
//    }
//
//    drawerLayout.closeDrawer(GravityCompat.START);
//    return true;
//}
//
//
//
//private void finishLoading() {
//    isLoading = false;
//    if (swipeRefreshLayout != null) {
//        swipeRefreshLayout.setRefreshing(false);
//        swipeRefreshLayout.setEnabled(true);
//    }
//}






