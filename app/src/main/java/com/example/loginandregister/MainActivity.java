package com.example.loginandregister;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {
    private TextView tvUsername;
    private MaterialButton btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvUsername = findViewById(R.id.tvUsername);
        btnLogout = findViewById(R.id.btnLogout);

        // 获取当前登录的用户名
        SharedPreferences sp = getSharedPreferences("user_info", MODE_PRIVATE);
        String currentUser = sp.getString("current_user", "");
        tvUsername.setText("当前用户：" + currentUser);

        // 设置退出按钮点击事件
        btnLogout.setOnClickListener(v -> {
            // 清除当前用户信息
            sp.edit().remove("current_user").apply();
            
            // 返回登录页面
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
} 