package com.example.loginandregister;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {
    // 显示当前用户名的文本控件
    private TextView tvUsername;
    // 退出登录按钮
    private MaterialButton btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 视图初始化，绑定布局中的控件
        tvUsername = findViewById(R.id.tvUsername); // 当前用户名显示
        btnLogout = findViewById(R.id.btnLogout);   // 退出登录按钮

        // 获取当前登录的用户名
        SharedPreferences sp = getSharedPreferences("user_info", MODE_PRIVATE);
        String currentUser = sp.getString("current_user", ""); // 当前登录用户名
        tvUsername.setText("当前用户：" + currentUser);

        // 退出登录按钮点击事件
        btnLogout.setOnClickListener(v -> {
            // 清除当前用户信息
            sp.edit().remove("current_user").apply();
            // 跳转回登录页面，并清空任务栈
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
} 