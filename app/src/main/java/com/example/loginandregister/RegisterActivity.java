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
    // 用户名输入框的外层布局（用于显示错误提示等）
    private TextInputLayout tilUsername;
    // 密码输入框的外层布局（用于显示错误提示等）
    private TextInputLayout tilPassword;
    // 确认密码输入框的外层布局（用于显示错误提示等）
    private TextInputLayout tilConfirmPassword;
    // 用户名输入框
    private TextInputEditText etUsername;
    // 密码输入框
    private TextInputEditText etPassword;
    // 确认密码输入框
    private TextInputEditText etConfirmPassword;
    // 注册按钮
    private MaterialButton btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // 视图初始化，绑定布局中的控件
        tilUsername = findViewById(R.id.tilUsername); // 用户名输入框外层
        tilPassword = findViewById(R.id.tilPassword); // 密码输入框外层
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword); // 确认密码输入框外层
        etUsername = findViewById(R.id.etUsername);   // 用户名输入框
        etPassword = findViewById(R.id.etPassword);   // 密码输入框
        etConfirmPassword = findViewById(R.id.etConfirmPassword); // 确认密码输入框
        btnRegister = findViewById(R.id.btnRegister); // 注册按钮

        // 注册按钮点击事件
        btnRegister.setOnClickListener(v -> register());
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
     * 注册逻辑，包含输入校验、密码校验、用户名唯一性校验等
     */
    private void register() {
        // 清除错误提示
        tilUsername.setError(null);
        tilPassword.setError(null);
        tilConfirmPassword.setError(null);

        // 获取输入内容
        String username = etUsername.getText().toString().trim(); // 用户名
        String password = etPassword.getText().toString().trim(); // 密码
        String confirmPassword = etConfirmPassword.getText().toString().trim(); // 确认密码

        // 校验用户名、密码、确认密码是否为空
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

        // 校验密码格式
        if (!isPasswordValid(password)) {
            tilPassword.setError("密码至少8位，必须包含数字和字母");
            return;
        }

        // 校验两次输入的密码是否一致
        if (!password.equals(confirmPassword)) {
            tilConfirmPassword.setError("两次输入的密码不一致");
            return;
        }

        // 校验用户名是否已存在
        SharedPreferences sp = getSharedPreferences("user_info", MODE_PRIVATE);
        if (sp.contains(username)) {
            tilUsername.setError("用户名已存在");
            return;
        }

        // 保存用户信息到 SharedPreferences
        sp.edit().putString(username, password).apply();

        // 注册成功，关闭当前页面返回登录页
        Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show();
        finish();
    }
} 