package com.example.loginandregister.viewmodel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.loginandregister.model.Person;
import com.example.loginandregister.utils.DebounceUtils;

/**
 * 主页面的ViewModel，负责当前用户信息、退出登录和跳转到详情页的Person对象管理。
 * 通过LiveData将用户名、退出事件和跳转对象通知UI。
 */
public class HomeViewModel extends AndroidViewModel {
    private static final String TAG = "HomeViewModel";
    // 当前用户名
    private final MutableLiveData<String> currentUsername = new MutableLiveData<>();
    // 退出登录事件
    private final MutableLiveData<Boolean> logoutEvent = new MutableLiveData<>();
    // LiveData用于暴露要跳转的Person对象
    private final MutableLiveData<Person> jumpPersonLiveData = new MutableLiveData<>();
    // 用户名和年龄错误提示
    private final MutableLiveData<String> usernameError = new MutableLiveData<>();
    // 年龄错误提示
    private final MutableLiveData<String> ageError = new MutableLiveData<>();
    // 防抖按钮ID
    private static final int LOGOUT_BUTTON_ID = 1;
    private static final int DETAIL_BUTTON_ID = 2;

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
     * 获取用户名和年龄错误提示的LiveData
     */
    public LiveData<String> getUsernameError() { return usernameError; }
    /**
     * 获取年龄错误提示的LiveData
     */
    public LiveData<String> getAgeError() { return ageError; }

    /**
     * 加载当前登录用户。
     */
    public void loadCurrentUser() {
        Log.d(TAG, "loadCurrentUser: 开始加载当前用户");
        SharedPreferences sp = getApplication().getSharedPreferences("user_info", Context.MODE_PRIVATE);
        String user = sp.getString("current_user", "");
        Log.d(TAG, "loadCurrentUser: 当前用户=" + user);
        currentUsername.setValue(user);
        Log.d(TAG, "loadCurrentUser: 当前用户加载完成");
    }

    /**
     * 退出登录，清除当前用户信息并通知UI。
     */
    public void logout() {
        Log.d(TAG, "logout: 开始退出登录流程");
        // 防抖检查
        if (!DebounceUtils.canClick(String.valueOf(LOGOUT_BUTTON_ID))) {
            Log.w(TAG, "logout: 防抖检查失败，阻止重复点击");
            return;
        }
        
        Log.d(TAG, "logout: 防抖检查通过，开始设置防抖");
        // 设置防抖状态
        DebounceUtils.setDebounce(String.valueOf(LOGOUT_BUTTON_ID), () -> {
            // 防抖结束后的回调
            Log.d(TAG, "logout: 防抖结束回调");
        });
        
        Log.d(TAG, "logout: 清除当前用户信息");
        SharedPreferences sp = getApplication().getSharedPreferences("user_info", Context.MODE_PRIVATE);
        sp.edit().remove("current_user").apply();
        logoutEvent.setValue(true);
        // 清除防抖状态
        DebounceUtils.clearDebounce(String.valueOf(LOGOUT_BUTTON_ID));
        Log.d(TAG, "logout: 退出登录流程完成");
    }

    /**
     * 设置跳转到详情页面的Person对象，包含防抖检查。
     * @param person 要传递的Person对象
     */
    public void setJumpPerson(Person person) {
        Log.d(TAG, "setJumpPerson: 开始设置跳转Person对象，姓名=" + person.getName() + ", 年龄=" + person.getAge());
        usernameError.setValue(null);
        ageError.setValue(null);

        // 检查用户名是否为空
        if (person.getName() == null || person.getName().isEmpty()) {
            Log.d(TAG, "setJumpPerson: 用户名为空");
            usernameError.setValue("请输入用户名");
            return;
        }

        // 检查年龄是否有效
        if (person.getAge() <= 0) {
            Log.d(TAG, "setJumpPerson: 年龄无效");
            ageError.setValue("请输入有效年龄");
            return;
        }
        
        // 防抖检查
        if (!DebounceUtils.canClick(String.valueOf(DETAIL_BUTTON_ID))) {
            Log.w(TAG, "setJumpPerson: 防抖检查失败，阻止重复点击");
            return;
        }
        
        Log.d(TAG, "setJumpPerson: 防抖检查通过，开始设置防抖");
        // 设置防抖状态
        DebounceUtils.setDebounce(String.valueOf(DETAIL_BUTTON_ID), () -> {
            // 防抖结束后的回调
            Log.d(TAG, "setJumpPerson: 防抖结束回调");
        });
        
        jumpPersonLiveData.setValue(person);
        // 清除防抖状态
        DebounceUtils.clearDebounce(String.valueOf(DETAIL_BUTTON_ID));
        Log.d(TAG, "setJumpPerson: 跳转Person对象设置完成");
    }
}
