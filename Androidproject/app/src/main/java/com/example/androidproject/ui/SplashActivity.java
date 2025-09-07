package com.example.androidproject.ui;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.androidproject.ui.auth.LoginActivity;
import com.example.androidproject.ui.email.InboxActivity;
import com.example.androidproject.viewModel.SplashViewModel;

public class SplashActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SplashViewModel vm = new ViewModelProvider(this).get(SplashViewModel.class);
        vm.isSignedIn.observe(this, ok -> {
            Intent intent = new Intent(this, Boolean.TRUE.equals(ok) ? InboxActivity.class : LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
        vm.checkSession();
    }
}