package com.example.loginandregister;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class LoginActivity extends AppCompatActivity {
    private TextInputLayout tilUsername, tilPassword;
    private AutoCompleteTextView etUsername;
    private TextInputEditText etPassword;
    private MaterialButton btnLogin;
    private MaterialCheckBox cbRemember;

    private static final int MAX_RECENT_USERS = 5;  // 最多保存5个最近用户

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 初始化视图
        tilUsername = findViewById(R.id.tilUsername);
        tilPassword = findViewById(R.id.tilPassword);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        cbRemember = findViewById(R.id.cbRemember);

        // 设置最近用户列表
        setupRecentUsers();

        // 检查是否有保存的登录信息
        SharedPreferences sp = getSharedPreferences("user_info", MODE_PRIVATE);
        boolean isRemembered = sp.getBoolean("remember_password", false);
        if (isRemembered) {
            String savedUsername = sp.getString("saved_username", "");
            String savedPassword = sp.getString("saved_password", "");
            etUsername.setText(savedUsername);
            etPassword.setText(savedPassword);
            cbRemember.setChecked(true);
        }

        // 设置点击事件
        btnLogin.setOnClickListener(v -> login());
        findViewById(R.id.tvRegister).setOnClickListener(v -> 
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class))
        );

        // 设置用户名选择监听
        etUsername.setOnItemClickListener((parent, view, position, id) -> {
            String selectedUsername = parent.getItemAtPosition(position).toString();
            if (sp.getBoolean("remember_password", false)) {
                String savedPassword = sp.getString(selectedUsername + "_password", "");
                etPassword.setText(savedPassword);
                cbRemember.setChecked(true);
            }
        });
    }

    private void setupRecentUsers() {
        SharedPreferences sp = getSharedPreferences("user_info", MODE_PRIVATE);
        Set<String> recentUsers = sp.getStringSet("recent_users", new HashSet<>());
        
        ArrayList<String> userList = new ArrayList<>(recentUsers);
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, userList);
        etUsername.setAdapter(adapter);
    }

    private boolean isPasswordValid(String password) {
        if (password.length() < 8) return false;
        boolean hasLetter = false;
        boolean hasDigit = false;
        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) hasLetter = true;
            if (Character.isDigit(c)) hasDigit = true;
        }
        return hasLetter && hasDigit;
    }

    private void updateRecentUsers(String username) {
        SharedPreferences sp = getSharedPreferences("user_info", MODE_PRIVATE);
        Set<String> recentUsers = new HashSet<>(sp.getStringSet("recent_users", new HashSet<>()));
        
        // 将新用户添加到列表开头
        recentUsers.remove(username);  // 如果已存在，先移除
        recentUsers.add(username);     // 添加到集合中
        
        // 如果超过最大数量，移除最早的用户
        if (recentUsers.size() > MAX_RECENT_USERS) {
            ArrayList<String> userList = new ArrayList<>(recentUsers);
            userList.remove(0);  // 移除最早的用户
            recentUsers = new HashSet<>(userList);
        }
        
        // 保存更新后的列表
        sp.edit().putStringSet("recent_users", recentUsers).apply();
    }

    private void login() {
        // 重置错误提示
        tilUsername.setError(null);
        tilPassword.setError(null);

        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // 验证输入
        if (TextUtils.isEmpty(username)) {
            tilUsername.setError("请输入用户名");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            tilPassword.setError("请输入密码");
            return;
        }

        if (!isPasswordValid(password)) {
            tilPassword.setError("密码至少8位，必须包含数字和字母");
            return;
        }

        // 验证用户名和密码
        SharedPreferences sp = getSharedPreferences("user_info", MODE_PRIVATE);
        String savedPassword = sp.getString(username, "");

        if (savedPassword.equals(password)) {
            // 更新最近用户列表
            updateRecentUsers(username);

            // 处理记住密码
            SharedPreferences.Editor editor = sp.edit();
            if (cbRemember.isChecked()) {
                editor.putBoolean("remember_password", true);
                editor.putString("saved_username", username);
                editor.putString("saved_password", password);
                editor.putString(username + "_password", password);  // 为每个用户保存密码
            } else {
                editor.putBoolean("remember_password", false);
                editor.remove("saved_username");
                editor.remove("saved_password");
                editor.remove(username + "_password");
            }
            editor.putString("current_user", username);
            editor.apply();
            
            // 登录成功
            Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            // 登录失败
            Toast.makeText(this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
        }
    }
} 