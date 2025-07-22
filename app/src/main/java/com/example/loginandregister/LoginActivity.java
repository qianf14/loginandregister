package com.example.loginandregister;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.loginandregister.viewmodel.LoginViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {
    private TextInputLayout tilUsername;
    private TextInputLayout tilPassword;
    private AutoCompleteTextView etUsername;
    private TextInputEditText etPassword;
    private MaterialButton btnLogin;
    private MaterialCheckBox cbRemember;
    private LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tilUsername = findViewById(R.id.tilUsername);
        tilPassword = findViewById(R.id.tilPassword);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        cbRemember = findViewById(R.id.cbRemember);

        loginViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())).get(LoginViewModel.class);

        // 观察LiveData
        loginViewModel.getUsernameError().observe(this, error -> tilUsername.setError(error));
        loginViewModel.getPasswordError().observe(this, error -> tilPassword.setError(error));
        loginViewModel.getToastMessage().observe(this, msg -> {
            if (msg != null) Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        });
        loginViewModel.getLoginResult().observe(this, result -> {
            if (result != null && result) {
                startActivity(new Intent(this, HomeActivity.class));
                finish();
            }
        });
        loginViewModel.getRecentUsers().observe(this, users -> {
            if (users != null) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, users);
                etUsername.setAdapter(adapter);
            }
        });
        loginViewModel.getAutoFillPassword().observe(this, pwd -> {
            if (pwd != null) etPassword.setText(pwd);
        });

        // 加载最近用户
        loginViewModel.loadRecentUsers();

        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            boolean remember = cbRemember.isChecked();
            loginViewModel.login(username, password, remember);
        });

        findViewById(R.id.tvRegister).setOnClickListener(v ->
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class))
        );

        etUsername.setOnItemClickListener((parent, view, position, id) -> {
            String selectedUsername = parent.getItemAtPosition(position).toString();
            loginViewModel.onUsernameSelected(selectedUsername);
        });
    }
} 