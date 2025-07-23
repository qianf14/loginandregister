package com.example.loginandregister.viewmodel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

/**
 * 主页面(HomeActivity)的ViewModel，负责当前用户信息的获取和退出登录逻辑。
 * 通过LiveData将用户名和退出事件通知UI。
 */
public class MainViewModel extends AndroidViewModel {
    // 当前用户名
    private final MutableLiveData<String> currentUsername = new MutableLiveData<>();
    // 退出登录事件
    private final MutableLiveData<Boolean> logoutEvent = new MutableLiveData<>();

    /**
     * 构造方法，初始化并加载当前用户。
     */
    public MainViewModel(@NonNull Application application) {
        super(application);
        loadCurrentUser();
    }

    public LiveData<String> getCurrentUsername() { return currentUsername; }
    public LiveData<Boolean> getLogoutEvent() { return logoutEvent; }

    /**
     * 加载当前登录用户。
     */
    public void loadCurrentUser() {
        SharedPreferences sp = getApplication().getSharedPreferences("user_info", Context.MODE_PRIVATE);
        String user = sp.getString("current_user", "");
        currentUsername.setValue(user);
    }

    /**
     * 退出登录，清除当前用户信息并通知UI。
     */
    public void logout() {
        SharedPreferences sp = getApplication().getSharedPreferences("user_info", Context.MODE_PRIVATE);
        sp.edit().remove("current_user").apply();
        logoutEvent.setValue(true);
    }
} 