package com.example.loginandregister;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterActivity extends AppCompatActivity {
    private TextInputLayout tilUsername, tilPassword, tilConfirmPassword;
    private TextInputEditText etUsername, etPassword, etConfirmPassword;
    private MaterialButton btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // 初始化视图
        tilUsername = findViewById(R.id.tilUsername);
        tilPassword = findViewById(R.id.tilPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);

        // 设置点击事件
        btnRegister.setOnClickListener(v -> register());
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

    private void register() {
        // 重置错误提示
        tilUsername.setError(null);
        tilPassword.setError(null);
        tilConfirmPassword.setError(null);

        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // 验证输入
        if (TextUtils.isEmpty(username)) {
            tilUsername.setError("请输入用户名");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            tilPassword.setError("请输入密码");
            return;
        }
        if (TextUtils.isEmpty(confirmPassword)) {
            tilConfirmPassword.setError("请确认密码");
            return;
        }

        // 验证密码规则
        if (!isPasswordValid(password)) {
            tilPassword.setError("密码至少8位，必须包含数字和字母");
            return;
        }

        if (!password.equals(confirmPassword)) {
            tilConfirmPassword.setError("两次输入的密码不一致");
            return;
        }

        // 检查用户名是否已存在
        SharedPreferences sp = getSharedPreferences("user_info", MODE_PRIVATE);
        if (sp.contains(username)) {
            tilUsername.setError("用户名已存在");
            return;
        }

        // 保存用户信息
        sp.edit().putString(username, password).apply();

        // 注册成功
        Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show();
        finish();
    }
} 