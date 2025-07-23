package com.example.loginandregister;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.loginandregister.viewmodel.MainViewModel;
import com.google.android.material.button.MaterialButton;

/**
 * 主页面Activity，负责显示当前登录用户和退出登录功能。
 * 通过ViewModel实现业务逻辑与UI解耦。
 */
public class HomeActivity extends AppCompatActivity {
    // 当前用户名显示控件
    private TextView tvUsername;
    // 退出登录按钮
    private MaterialButton btnLogout;
    // 主页面业务ViewModel
    private MainViewModel mainViewModel;

    /**
     * Activity生命周期-创建，初始化UI和ViewModel，设置事件监听。
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 绑定布局控件
        tvUsername = findViewById(R.id.tvUsername);
        btnLogout = findViewById(R.id.btnLogout);
        // 初始化ViewModel
        mainViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())).get(MainViewModel.class);

        // 观察ViewModel的LiveData，自动响应UI变化
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

        // 退出登录按钮点击事件，调用ViewModel退出方法
        btnLogout.setOnClickListener(v -> mainViewModel.logout());
    }
} 