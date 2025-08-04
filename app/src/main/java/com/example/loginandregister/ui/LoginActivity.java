package com.example.loginandregister.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.loginandregister.R;
import com.example.loginandregister.viewmodel.LoginViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.ArrayList;

/**
 * 登录页面Activity，负责用户登录界面的UI展示和交互。
 * 通过ViewModel实现业务逻辑与UI解耦。
 */
public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    // 用户名输入框外层布局（用于错误提示）
    private TextInputLayout tilUsername;
    // 密码输入框外层布局（用于错误提示）
    private TextInputLayout tilPassword;
    // 用户名输入框，支持下拉自动补全
    private AutoCompleteTextView etUsername;
    // 密码输入框
    private TextInputEditText etPassword;
    // 登录按钮
    private MaterialButton btnLogin;
    // 记住密码复选框
    private MaterialCheckBox cbRemember;
    // 登录业务ViewModel
    private LoginViewModel loginViewModel;

    /**
     * Activity生命周期-创建，初始化UI和ViewModel，设置事件监听。
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 绑定布局控件
        tilUsername = findViewById(R.id.tilUsername);
        tilPassword = findViewById(R.id.tilPassword);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        cbRemember = findViewById(R.id.cbRemember);

        // 初始化ViewModel
        loginViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())).get(LoginViewModel.class);

        // 观察ViewModel的LiveData，自动响应UI变化
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

        // 加载最近用户列表
        loginViewModel.loadRecentUsers();

        // 登录按钮点击事件，调用ViewModel登录方法
        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            boolean remember = cbRemember.isChecked();
            loginViewModel.login(username, password, remember);
        });

        // 注册按钮点击事件，跳转注册页面
        findViewById(R.id.tvRegister).setOnClickListener(v ->
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class))
        );

        // 用户名下拉选择事件，自动填充密码
        etUsername.setOnItemClickListener((parent, view, position, id) -> {
            String selectedUsername = parent.getItemAtPosition(position).toString();
            loginViewModel.onUsernameSelected(selectedUsername);
        });
    }
}
