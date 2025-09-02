package com.example.androidproject.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.androidproject.ui.email.HomeActivity;
import com.example.androidproject.R;
import com.example.androidproject.util.TokenManager;
import com.example.androidproject.util.ValidationUtils;
import com.example.androidproject.ui.auth.LoginViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * LoginActivity handles user login functionality.
 * It validates user input, communicates with the backend API,
 * and navigates to the HomeActivity upon successful login.
 */
public class LoginActivity extends AppCompatActivity {

    private TextInputLayout tilUsername, tilPassword;
    private TextInputEditText etUsername, etPassword;
    private ProgressBar progressBar;
    private LoginViewModel loginViewModel;

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
        MaterialButton btnSignIn = findViewById(R.id.btn_sign_in);
        MaterialButton btnCreateAccount = findViewById(R.id.btn_create_account);
        MaterialButton btnForgotPassword = findViewById(R.id.btn_forgot_password);

        btnSignIn.setOnClickListener(v -> validateAndSignIn());
        btnCreateAccount.setOnClickListener(v -> navigateToRegistration());
        btnForgotPassword.setOnClickListener(v -> handleForgotPassword());
        // Initialize ViewModel
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        // Observe ViewModel LiveData
        loginViewModel.getLoginResult().observe(this, loginResponse -> {
            showLoading(false);
            if (loginResponse != null && loginResponse.getUser() != null) {
                Toast.makeText(LoginActivity.this,
                        "Login successful", Toast.LENGTH_SHORT).show();
                TokenManager.saveUserInfo(
                        this,
                        loginResponse.getUser().getUsername(),
                        loginResponse.getUser().getFirstName(),
                        loginResponse.getUser().getSurName(),
                        loginResponse.getUser().getPicture()
                );
                // Save the token
                if (loginResponse.getToken() != null) {
                    TokenManager.saveAuthToken(this, loginResponse.getToken());
                }
                // Navigate to the Home activity
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
        loginViewModel.getErrorMessage().observe(this, errorMsg -> {
            showLoading(false);
            if (errorMsg != null) {
                Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_LONG).show();
            }
        });
        loginViewModel.getLoading().observe(this, this::showLoading);
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

        if (isValid) {
            performLogin(username, password);
        }
    }

    /**
     * Performs the login operation by calling the backend API.
     * @param username The username entered by the user.
     * @param password The password entered by the user.
     */
    private void performLogin (String username, String password) {
        loginViewModel.login(username, password);
    }


    /**
     * Navigates to the registration activity.
     */
    private void navigateToRegistration() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * Handles the forgot password functionality.
     * Currently, it shows a placeholder message.
     */
    private void handleForgotPassword() {
        // TODO: Implement forgot password flow
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