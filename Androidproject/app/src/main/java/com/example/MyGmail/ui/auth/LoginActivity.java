package com.example.MyGmail.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.MyGmail.R;
import com.example.MyGmail.data.models.User;
import com.example.MyGmail.data.remote.net.ApiClient;
import com.example.MyGmail.ui.email.MailsActivity;
import com.example.MyGmail.util.ValidationUtils;
import com.example.MyGmail.viewModel.UserViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * LoginActivity handles user login functionality.
 * It validates user input, communicates with the backend API,
 * and navigates to the InboxActivity upon successful login.
 */
public class LoginActivity extends AppCompatActivity {

    private TextInputLayout tilUsername, tilPassword;
    private TextInputEditText etUsername, etPassword;
    private ProgressBar progressBar;
    private UserViewModel userViewModel;
    MaterialButton btnSignIn;
    private boolean didNavigate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Initialize views
        tilUsername = findViewById(R.id.til_email);
        tilPassword = findViewById(R.id.til_login_password);
        etUsername = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_login_password);
        progressBar = findViewById(R.id.progress_bar);

        // Set up button clicks
        btnSignIn = findViewById(R.id.btn_sign_in);
        MaterialButton btnCreateAccount = findViewById(R.id.btn_create_account);
        MaterialButton btnForgotPassword = findViewById(R.id.btn_forgot_password);

        btnSignIn.setOnClickListener(v -> validateAndSignIn());
        btnCreateAccount.setOnClickListener(v -> navigateToRegistration());
        btnForgotPassword.setOnClickListener(v -> handleForgotPassword());

        // Initialize ViewModel
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // If a user exists in Room (auto sign-in via cookie already happened), go to inbox
        userViewModel.getUser().observe(this, (User user) -> {
            if (didNavigate) return;
            boolean hasSession = ApiClient.hasSession(this);
            if (hasSession && user != null && user.username != null && !user.username.isEmpty()) {
                didNavigate = true;
                showLoading(false);
                btnSignIn.setEnabled(true);
                Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MailsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        // Observe ViewModel LiveData errors
        userViewModel.getErrorMessage().observe(this, msg -> {
            showLoading(false);
            btnSignIn.setEnabled(true);
            if (msg != null && !msg.isEmpty()) {
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Validates user input and initiates the sign-in process if valid.
     */
    private void validateAndSignIn() {
        // Clear previous errors
        tilUsername.setError(null);
        tilPassword.setError(null);

        String username = etUsername.getText() == null ? "" : etUsername.getText().toString().trim();
        String password = etPassword.getText() == null ? "" : etPassword.getText().toString();

        boolean isValid = true;

        if (username.isEmpty()) {
            tilUsername.setError("Enter your username");
            isValid = false;
        } else if (!ValidationUtils.isValidUsername(username)) {
            tilUsername.setError("Enter a valid username");
            isValid = false;
        }

        if (password.isEmpty()) {
            tilPassword.setError("Enter a password");
            isValid = false;
        }
        if (!isValid) return;
        showLoading(true);
        btnSignIn.setEnabled(false);
        userViewModel.login(username, password);
    }

    /**
     * Navigates to the registration activity.
     */
    private void navigateToRegistration() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    /**
     * Handles the forgot password functionality.
     * Currently, it shows a placeholder message.
     */
    private void handleForgotPassword() {
        Toast.makeText(this, "Forgot password clicked", Toast.LENGTH_SHORT).show();
    }

    /**
     * Shows or hides the loading indicator.
     * @param isLoading True to show the loading indicator, false to hide it.
     */
    private void showLoading(boolean isLoading) {
        if (progressBar != null) {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
    }
}