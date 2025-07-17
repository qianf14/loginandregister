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
    // 用户名输入框的外层布局（用于显示错误提示等）
    private TextInputLayout tilUsername;
    // 密码输入框的外层布局（用于显示错误提示等）
    private TextInputLayout tilPassword;
    // 用户名输入框，支持下拉自动补全最近用户
    private AutoCompleteTextView etUsername;
    // 密码输入框
    private TextInputEditText etPassword;
    // 登录按钮
    private MaterialButton btnLogin;
    // 记住密码复选框
    private MaterialCheckBox cbRemember;

    // 最多保存的最近用户数量
    private static final int MAX_RECENT_USERS = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 视图初始化，绑定布局中的控件
        tilUsername = findViewById(R.id.tilUsername); // 用户名输入框外层
        tilPassword = findViewById(R.id.tilPassword); // 密码输入框外层
        etUsername = findViewById(R.id.etUsername);   // 用户名输入框
        etPassword = findViewById(R.id.etPassword);   // 密码输入框
        btnLogin = findViewById(R.id.btnLogin);       // 登录按钮
        cbRemember = findViewById(R.id.cbRemember);   // 记住密码复选框

        // 设置最近用户下拉列表
        setupRecentUsers();

        // 检查是否有保存的登录信息（记住密码）
        SharedPreferences sp = getSharedPreferences("user_info", MODE_PRIVATE);
        boolean isRemembered = sp.getBoolean("remember_password", false);
        if (isRemembered) {
            // 如果记住密码，自动填充用户名和密码
            String savedUsername = sp.getString("saved_username", "");
            String savedPassword = sp.getString("saved_password", "");
            etUsername.setText(savedUsername);
            etPassword.setText(savedPassword);
            cbRemember.setChecked(true);
        }

        // 登录按钮点击事件
        btnLogin.setOnClickListener(v -> login());
        // 注册按钮点击事件，跳转到注册页面
        findViewById(R.id.tvRegister).setOnClickListener(v ->
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class))
        );

        // 用户名下拉选择事件，自动填充密码（如果有记住密码）
        etUsername.setOnItemClickListener((parent, view, position, id) -> {
            String selectedUsername = parent.getItemAtPosition(position).toString();
            if (sp.getBoolean("remember_password", false)) {
                String savedPassword = sp.getString(selectedUsername + "_password", "");
                etPassword.setText(savedPassword);
                cbRemember.setChecked(true);
            }
        });
    }

    /**
     * 设置最近登录用户的下拉列表
     * 从 SharedPreferences 读取最近用户集合，设置到 AutoCompleteTextView
     */
    private void setupRecentUsers() {
        SharedPreferences sp = getSharedPreferences("user_info", MODE_PRIVATE);
        Set<String> recentUsers = sp.getStringSet("recent_users", new HashSet<>()); // 最近用户集合
        ArrayList<String> userList = new ArrayList<>(recentUsers); // 转为列表用于适配器
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, userList);
        etUsername.setAdapter(adapter);
    }

    /**
     * 密码格式校验，要求至少8位且包含字母和数字
     * @param password 用户输入的密码
     * @return 是否符合规则
     */
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

    /**
     * 更新最近登录用户列表
     * @param username 当前登录成功的用户名
     */
    private void updateRecentUsers(String username) {
        SharedPreferences sp = getSharedPreferences("user_info", MODE_PRIVATE);
        Set<String> recentUsers = new HashSet<>(sp.getStringSet("recent_users", new HashSet<>()));
        // 先移除已存在的用户名，保证最新的在最后
        recentUsers.remove(username);
        recentUsers.add(username);
        // 超过最大数量则移除最早的
        if (recentUsers.size() > MAX_RECENT_USERS) {
            ArrayList<String> userList = new ArrayList<>(recentUsers);
            userList.remove(0);
            recentUsers = new HashSet<>(userList);
        }
        sp.edit().putStringSet("recent_users", recentUsers).apply();
    }

    /**
     * 登录逻辑，包含输入校验、密码校验、记住密码、最近用户更新等
     */
    private void login() {
        // 清除错误提示
        tilUsername.setError(null);
        tilPassword.setError(null);

        // 获取输入内容
        String username = etUsername.getText().toString().trim(); // 用户名
        String password = etPassword.getText().toString().trim(); // 密码

        // 校验用户名和密码是否为空
        if (TextUtils.isEmpty(username)) {
            tilUsername.setError("请输入用户名");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            tilPassword.setError("请输入密码");
            return;
        }

        // 校验密码格式
        if (!isPasswordValid(password)) {
            tilPassword.setError("密码至少8位，必须包含数字和字母");
            return;
        }

        // 校验用户名和密码是否匹配
        SharedPreferences sp = getSharedPreferences("user_info", MODE_PRIVATE);
        String savedPassword = sp.getString(username, ""); // 取出保存的密码

        if (savedPassword.equals(password)) {
            // 登录成功，更新最近用户列表
            updateRecentUsers(username);

            // 处理记住密码逻辑
            SharedPreferences.Editor editor = sp.edit();
            if (cbRemember.isChecked()) {
                editor.putBoolean("remember_password", true); // 标记记住密码
                editor.putString("saved_username", username); // 保存用户名
                editor.putString("saved_password", password); // 保存密码
                editor.putString(username + "_password", password); // 为每个用户单独保存密码
            } else {
                editor.putBoolean("remember_password", false);
                editor.remove("saved_username");
                editor.remove("saved_password");
                editor.remove(username + "_password");
            }
            editor.putString("current_user", username); // 保存当前登录用户
            editor.apply();

            // 跳转到主页面
            Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            // 登录失败，用户名或密码错误
            Toast.makeText(this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
        }
    }
} 