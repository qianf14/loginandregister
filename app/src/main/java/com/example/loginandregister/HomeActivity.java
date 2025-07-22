package com.example.loginandregister;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.loginandregister.viewmodel.MainViewModel;
import com.google.android.material.button.MaterialButton;

public class HomeActivity extends AppCompatActivity {
    private TextView tvUsername;
    private MaterialButton btnLogout;
    private MainViewModel mainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvUsername = findViewById(R.id.tvUsername);
        btnLogout = findViewById(R.id.btnLogout);
        mainViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())).get(MainViewModel.class);

        mainViewModel.getCurrentUsername().observe(this, username -> {
            tvUsername.setText("当前用户：" + username);
        });
        mainViewModel.getLogoutEvent().observe(this, logout -> {
            if (logout != null && logout) {
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        btnLogout.setOnClickListener(v -> mainViewModel.logout());
    }
} 