package com.example.loginandregister;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.loginandregister.viewmodel.HomeViewModel;
import com.example.loginandregister.model.Person;
import com.google.android.material.button.MaterialButton;

/**
 * 主页面Activity，负责显示当前登录用户和跳转到详情页面。
 * 通过HomeViewModel管理所有主页面相关数据和事件，MVVM解耦。
 */
public class HomeActivity extends AppCompatActivity {
    // 当前用户名显示控件
    private TextView tvUsername;
    // 退出登录按钮
    private MaterialButton btnLogout;
    // 跳转详情按钮
    private MaterialButton btnDetail;
    // 主页面ViewModel（合并后的）
    private HomeViewModel homeViewModel;

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
        btnDetail = findViewById(R.id.btnDetail);
        // 初始化合并后的HomeViewModel
        homeViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())).get(HomeViewModel.class);

        // 观察ViewModel的LiveData，自动响应UI变化
        homeViewModel.getCurrentUsername().observe(this, username -> {
            tvUsername.setText("当前用户：" + username);
        });
        homeViewModel.getLogoutEvent().observe(this, logout -> {
            if (logout != null && logout) {
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
        // 观察HomeViewModel的跳转Person对象，触发跳转
        homeViewModel.getJumpPerson().observe(this, person -> {
            if (person != null) {
                Intent intent = new Intent(this, DetailActivity.class);
                intent.putExtra("person", person);
                startActivity(intent);
            }
        });
        // 退出登录按钮点击事件，调用ViewModel退出方法
        btnLogout.setOnClickListener(v -> homeViewModel.logout());
        // 跳转详情按钮点击事件，设置Person对象到ViewModel
        btnDetail.setOnClickListener(v -> {
            // 这里可根据实际业务动态生成Person对象
            Person person = new Person("张三", 25);
            homeViewModel.setJumpPerson(person);
        });
    }
} 