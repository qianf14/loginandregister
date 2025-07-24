package com.example.loginandregister.viewmodel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.loginandregister.model.Person;

/**
 * 主页面的ViewModel，负责当前用户信息、退出登录和跳转到详情页的Person对象管理。
 * 通过LiveData将用户名、退出事件和跳转对象通知UI。
 */
public class HomeViewModel extends AndroidViewModel {
    // 当前用户名
    private final MutableLiveData<String> currentUsername = new MutableLiveData<>();
    // 退出登录事件
    private final MutableLiveData<Boolean> logoutEvent = new MutableLiveData<>();
    // LiveData用于暴露要跳转的Person对象
    private final MutableLiveData<Person> jumpPersonLiveData = new MutableLiveData<>();

    /**
     * 构造方法，初始化并加载当前用户。
     */
    public HomeViewModel(@NonNull Application application) {
        super(application);
        loadCurrentUser();
    }

    /**
     * 获取当前用户名的LiveData
     */
    public LiveData<String> getCurrentUsername() { return currentUsername; }
    /**
     * 获取退出登录事件的LiveData
     */
    public LiveData<Boolean> getLogoutEvent() { return logoutEvent; }
    /**
     * 获取要跳转的Person对象的LiveData
     */
    public LiveData<Person> getJumpPerson() { return jumpPersonLiveData; }

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

    /**
     * 设置要跳转的Person对象
     * @param person Person对象
     */
    public void setJumpPerson(Person person) {
        jumpPersonLiveData.setValue(person);
    }
} 