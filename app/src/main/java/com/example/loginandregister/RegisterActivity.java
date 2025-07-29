package com.example.loginandregister;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.loginandregister.viewmodel.RegisterViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * 注册页面Activity，负责用户注册界面的UI展示和交互。
 * 通过ViewModel实现业务逻辑与UI解耦。
 */
public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    // 用户名输入框外层布局（用于错误提示）
    private TextInputLayout tilUsername;
    // 密码输入框外层布局（用于错误提示）
    private TextInputLayout tilPassword;
    // 确认密码输入框外层布局（用于错误提示）
    private TextInputLayout tilConfirmPassword;
    // 用户名输入框
    private TextInputEditText etUsername;
    // 密码输入框
    private TextInputEditText etPassword;
    // 确认密码输入框
    private TextInputEditText etConfirmPassword;
    // 注册按钮
    private MaterialButton btnRegister;
    // 注册业务ViewModel
    private RegisterViewModel registerViewModel;

    /**
     * Activity生命周期-创建，初始化UI和ViewModel，设置事件监听。
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // 绑定布局控件
        tilUsername = findViewById(R.id.tilUsername);
        tilPassword = findViewById(R.id.tilPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);

        // 初始化ViewModel
        registerViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())).get(RegisterViewModel.class);

        // 观察ViewModel的LiveData，自动响应UI变化
        registerViewModel.getUsernameError().observe(this, error -> tilUsername.setError(error));
        registerViewModel.getPasswordError().observe(this, error -> tilPassword.setError(error));
        registerViewModel.getConfirmPasswordError().observe(this, error -> tilConfirmPassword.setError(error));
        registerViewModel.getToastMessage().observe(this, msg -> {
            if (msg != null) Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        });
        registerViewModel.getRegisterResult().observe(this, result -> {
            if (result != null && result) {
                finish();
            }
        });

        // 注册按钮点击事件，调用ViewModel注册方法
        btnRegister.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();
            registerViewModel.register(username, password, confirmPassword);
        });
    }
}
