package com.example.MyGmail.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.MyGmail.ui.email.MailsActivity;
import com.example.MyGmail.viewModel.UserViewModel;

public class SplashActivity extends AppCompatActivity {
    private boolean hasNavigated = false;
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UserViewModel vm = new ViewModelProvider(this).get(UserViewModel.class);
        vm.getUser().observe(this, user -> {
            if (hasNavigated) return;
            hasNavigated = true;
            Intent intent = new Intent(this, user != null ? MailsActivity.class : LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
        vm.getErrorMessage().observe(this, err -> {
            if (hasNavigated) return;
            hasNavigated = true;
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
        if (vm.hasSession()) {
            vm.refreshMe();
        } else {
            vm.logout();
        }
    }
}