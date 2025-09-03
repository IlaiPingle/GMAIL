package com.example.androidproject.ui.auth;

import android.Manifest;
import android.content.Intent;
import android.content.ContentUris;
import android.database.Cursor;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.DocumentsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.androidproject.ui.email.HomeActivity;
import com.example.androidproject.R;
import com.example.androidproject.util.TokenManager;
import com.example.androidproject.util.ValidationUtils;
import com.example.androidproject.data.remote.net.ApiClient;
import com.example.androidproject.api.ApiService;
import com.example.androidproject.model.LoginResponse;
import com.example.androidproject.model.RegisterResponse;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * MainActivity handles user registration including:
 * - Input fields for first name, last name, username, password, confirm password
 * - Profile photo selection from gallery
 * - Input validation with real-time feedback
 * - API call to register user with multipart form data (including image upload)
 * - Auto-login upon successful registration
 * - Navigation to LoginActivity if user opts to sign in instead
 */
public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 100;
    private ImageView imgProfile;
    private Uri selectedImageUri = null;

    private TextInputLayout tilFirstName, tilLastName, tilUsername, tilPassword, tilConfirmPassword;
    private TextInputEditText etFirstName, etLastName, etUsername, etPassword, etConfirmPassword;
    private ProgressBar progressBar;

    /**
     * Launcher for image picker activity result
     * Uses Activity Result API for better lifecycle handling
     */
    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    imgProfile.setImageURI(selectedImageUri);
                }
            }
    );

    // For permission request
    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    openImagePicker();
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        tilFirstName = findViewById(R.id.til_first_name);
        tilLastName = findViewById(R.id.til_last_name);
        tilUsername = findViewById(R.id.til_username);
        tilPassword = findViewById(R.id.til_password);
        tilConfirmPassword = findViewById(R.id.til_confirm_password);

        etFirstName = findViewById(R.id.et_first_name);
        etLastName = findViewById(R.id.et_last_name);
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);

        imgProfile = findViewById(R.id.img_profile);

        MaterialButton btnNext = findViewById(R.id.btn_next);
        btnNext.setOnClickListener(v -> validateAndSubmit());
        MaterialButton btnSelectPhoto = findViewById(R.id.btn_select_photo);
        btnSelectPhoto.setOnClickListener(v -> checkPermissionAndPickImage());
        MaterialButton btnSignInInstead = findViewById(R.id.btn_sign_in_top);
        btnSignInInstead.setOnClickListener(v -> {
            // Navigate to login activity
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish(); // Assuming this activity was started from LoginActivity
        });
        etUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0 && !ValidationUtils.isValidUsername(s.toString())) {
                    tilUsername.setError("Use only letters, numbers and periods");
                } else {
                    tilUsername.setHelperText(getString(R.string.helper_username));
                }
            }
        });
        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() >0) {
                    if (s.length() < 8) {
                        tilPassword.setError("Password is too short");
                    } else if (!ValidationUtils.isStrongPassword(s.toString())) {
                        tilPassword.setHelperText("Make password stronger");
                    } else {
                        tilPassword.setHelperText(getString(R.string.helper_password));
                    }
                }
            }
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
        });
    }

    /**
     * Check for storage permission and launch image picker
     * Handles different permission requirements for Android versions
     * Uses Activity Result API for permission request
     * Opens image picker if permission is granted
     * Otherwise, shows a toast message if permission is denied
     */
    private void checkPermissionAndPickImage() {
        String permission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permission = Manifest.permission.READ_MEDIA_IMAGES;
        } else {
            permission = Manifest.permission.READ_EXTERNAL_STORAGE;
        }

        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            openImagePicker();
        } else {
            requestPermissionLauncher.launch(permission);
        }
    }

    /**
     * Launches an intent to pick an image from the device's gallery
     * Uses Activity Result API to handle the result
     * Sets the selected image URI and updates the ImageView
     * to display the chosen profile photo
     */
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }
    /**
     * Validates user input fields and submits registration data if valid
     * Checks for empty fields, username format, password strength, and matching passwords
     * Displays error messages on invalid fields
     * Ensures a profile photo is selected before submission
     * Calls uploadData() to perform the API call if all validations pass
     */
    private void validateAndSubmit() {
        clearErrors();

        String first = getText(etFirstName);
        String last = getText(etLastName);
        String user = getText(etUsername);
        String pass = getText(etPassword);
        String pass2 = getText(etConfirmPassword);

        boolean ok = true;

        if (first.isEmpty()) {
            tilFirstName.setError("Required");
            ok = false;
        }
        if (last.isEmpty()) {
            tilLastName.setError("Required");
            ok = false;
        }
        if (user.isEmpty()) {
            tilUsername.setError("Required");
            ok = false;
        } else if (!ValidationUtils.isValidUsername(user)) {
            tilUsername.setError("Use only letters, numbers and periods");
            ok = false;
        }
        if (pass.isEmpty()) {
            tilPassword.setError("Required");
            ok = false;
        } else if (pass.length() < 8) {
            tilPassword.setError("At least 8 characters required");
            ok = false;
        } else if (!ValidationUtils.isStrongPassword(pass)) {
            tilPassword.setError("Include at least 1 uppercase letter, 1 lowercase letter, 1 number, and 1 special character");
            ok = false;
        }
        if (pass2.isEmpty()) {
            tilConfirmPassword.setError("Required");
            ok = false;
        } else if (!pass.equals(pass2)) {
            tilConfirmPassword.setError("Passwords do not match");
            ok = false;
        }

        if (!ok) return;
        if (selectedImageUri == null) {
            Toast.makeText(this, "Please select a profile photo", Toast.LENGTH_SHORT).show();
            return;
        }
        uploadData(first, last, user, pass, selectedImageUri);
    }

    /**
     * Uploads registration data to the server using a multipart form request
     * Includes text fields and the selected profile image
     * Handles API response and errors
     * On successful registration, saves user info and auto-logs in
     * Displays appropriate toast messages for success or failure
     * @param first    User's first name
     * @param last     User's last name
     * @param user     Desired username
     * @param pass     Desired password
     * @param imageUri URI of the selected profile image
     */
    private void uploadData(String first, String last, String user, String pass, Uri imageUri) {
        showLoading(true);
        // Create API service
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        // Create RequestBody instances
        RequestBody firstNameBody = RequestBody.create(MediaType.parse("text/plain"), first);
        RequestBody surNameBody = RequestBody.create(MediaType.parse("text/plain"), last);
        RequestBody usernameBody = RequestBody.create(MediaType.parse("text/plain"), user);
        RequestBody passwordBody = RequestBody.create(MediaType.parse("text/plain"), pass);

        // Prepare image file
        File file = new File(getFilePathFromUri(imageUri));
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("picture",
                file.getName(), requestFile);

        // Make API call
        Call<RegisterResponse> call = apiService.register(
                firstNameBody, surNameBody, usernameBody, passwordBody, imagePart);

        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    // Registration successful
                    Toast.makeText(MainActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                    RegisterResponse.User u = response.body().getUser();
                    if (u != null) {
                        TokenManager.saveUserInfo(
                                MainActivity.this,
                                u.getUsername(),
                                u.getFirstName(),
                                u.getSurName(),
                                u.getPicture()
                        );
                    }
                    // Auto-login after registration
                    autoLogin(user, pass);
                } else {
                    // Handle error
                    String errorMsg = "Registration failed";
                    if (response.errorBody() != null) {
                        try {
                            errorMsg = response.errorBody().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    Toast.makeText(MainActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                showLoading(false);
                Toast.makeText(MainActivity.this, "Network error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Automatically logs in the user after successful registration
     * Makes an API call to the login endpoint with provided credentials
     * On success, navigates to HomeActivity
     * On failure, navigates to LoginActivity
     * @param username The username to log in with
     * @param password The password to log in with
     */
    private void autoLogin(String username, String password) {
        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.login(username, password).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    startActivity(new Intent(MainActivity.this, HomeActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    /**
     * Clears all error messages from input fields
     * Resets the error state of TextInputLayouts
     */
    private void clearErrors() {
        tilFirstName.setError(null);
        tilLastName.setError(null);
        tilUsername.setError(null);
        tilPassword.setError(null);
        tilConfirmPassword.setError(null);
    }

    // Helper to get trimmed text from TextInputEditText
    private String getText(TextInputEditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }

    /**
     * Converts a content URI to an absolute file path
     * Handles different URI schemes and document providers
     * @param uri The content URI to convert
     * @return The absolute file path, or null if not found
     */
    public String getFilePathFromUri(Uri uri) {
        String filePath;
        if (DocumentsContract.isDocumentUri(this, uri)) {
            // For newer Android versions
            String documentId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = documentId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                filePath = getPathFromMediaStore(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.parseLong(documentId));
                filePath = getPathFromMediaStore(contentUri, null);
            } else {
                filePath = null;
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            filePath = getPathFromMediaStore(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            filePath = uri.getPath();
        } else {
            filePath = null;
        }
        return filePath;
    }

    /**
     * Queries the MediaStore to get the file path from a content URI
     * @param uri The content URI to query
     * @param selection Optional selection criteria
     * @return The file path as a string, or null if not found
     */
    private String getPathFromMediaStore(Uri uri, String selection) {
        String path = null;
        String[] projection = {MediaStore.Images.Media.DATA};
        try (Cursor cursor = getContentResolver().query(uri, projection, selection, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                path = cursor.getString(columnIndex);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }

    // Parse error message from response body
    private String parseError(ResponseBody errorBody) {
        try {
            return errorBody.string();
        } catch (IOException e) {
            e.printStackTrace();
            return "Unknown error";
        }
    }

    /**
     * Shows or hides the loading indicator (ProgressBar)
     * @param isLoading true to show loading, false to hide
     */
    private void showLoading(boolean isLoading) {
        progressBar = findViewById(R.id.progress_bar);
        if (progressBar != null) {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
    }
}