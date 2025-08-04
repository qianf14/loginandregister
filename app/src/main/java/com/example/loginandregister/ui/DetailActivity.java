package com.example.loginandregister.ui;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.loginandregister.R;
import com.example.loginandregister.model.Person;
import com.example.loginandregister.viewmodel.DetailViewModel;

/**
 * 详情页面Activity，负责展示Person对象的信息。
 * 通过ViewModel实现数据与UI解耦。
 */
public class DetailActivity extends AppCompatActivity {
    private static final String TAG = "DetailActivity";
    // 展示Person信息的TextView
    private TextView tvInfo;
    // 详情页面ViewModel
    private DetailViewModel detailViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // 绑定控件
        tvInfo = findViewById(R.id.tvInfo);
        // 初始化ViewModel
        detailViewModel = new ViewModelProvider(this).get(DetailViewModel.class);
        // 获取Intent传递的Person对象，并设置到ViewModel
        Person person = (Person) getIntent().getSerializableExtra("person");
        detailViewModel.setPerson(person);
        // 观察ViewModel中的Person数据，自动更新UI
        detailViewModel.getPerson().observe(this, p -> {
            if (p != null) {
                String info = "姓名：" + p.getName() + "\n年龄：" + p.getAge();
                tvInfo.setText(info);
            }
        });
    }
}
