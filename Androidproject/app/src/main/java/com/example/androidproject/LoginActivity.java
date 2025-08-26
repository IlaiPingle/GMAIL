package com.example.androidproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout tilUsername, tilPassword;
    private TextInputEditText etUsername, etPassword;
    private ProgressBar progressBar;

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
    }

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

    private void performLogin (String username, String password) {
        showLoading(true);
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<LoginResponse> call = apiService.login(username, password);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(LoginActivity.this,
                            "Login successful", Toast.LENGTH_SHORT).show();
                    LoginResponse.User user = response.body().getUser();
                    if (user != null) {
                        TokenManager.saveUserInfo(
                                LoginActivity.this,
                                user.getUsername(),
                                user.getFirstName(),
                                user.getSurName(),
                                user.getPicture()
                        );
                    }
                    // Navigate to the Home activity
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    String errorMsg = "Login failed";
                    if (response.errorBody() != null) {
                        try {
                            errorMsg = response.errorBody().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                showLoading(false);
                Toast.makeText(LoginActivity.this,
                        "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    private void navigateToRegistration() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void handleForgotPassword() {
        // TODO: Implement forgot password flow
        Toast.makeText(this, "Forgot password clicked", Toast.LENGTH_SHORT).show();
    }

    private String parseError (ResponseBody errorBody) {
        try {
            return errorBody.string();
        } catch (IOException e) {
            e.printStackTrace();
            return "An Unknown error occurred";
        }
    }

    private void showLoading(boolean isLoading) {
        if (progressBar != null) {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
    }
}