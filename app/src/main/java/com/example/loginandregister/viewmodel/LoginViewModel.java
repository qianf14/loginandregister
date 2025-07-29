package com.example.loginandregister.viewmodel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.loginandregister.model.User;
import com.example.loginandregister.repository.UserRepository;
import com.example.loginandregister.utils.DebounceUtils;
import com.example.loginandregister.utils.MD5Utils;
import com.example.loginandregister.utils.PasswordTimeUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * 登录页面的ViewModel，负责登录业务逻辑、输入校验和与数据库的交互。
 * 通过LiveData将状态和错误信息通知UI。
 * 支持MD5密码加密和带时效性的自动填充功能。
 */
public class LoginViewModel extends AndroidViewModel {
    private static final String TAG = "LoginViewModel";
    private static final int LOGIN_BUTTON_ID = 4;
    // 用户数据仓库
    private final UserRepository userRepository;
    // 登录结果，true表示登录成功
    private final MutableLiveData<Boolean> loginResult = new MutableLiveData<>();
    // 用户名输入错误提示
    private final MutableLiveData<String> usernameError = new MutableLiveData<>();
    // 密码输入错误提示
    private final MutableLiveData<String> passwordError = new MutableLiveData<>();
    // Toast消息提示
    private final MutableLiveData<String> toastMessage = new MutableLiveData<>();
    // 最近登录用户列表
    private final MutableLiveData<ArrayList<String>> recentUsers = new MutableLiveData<>();
    // 自动填充密码
    private final MutableLiveData<String> autoFillPassword = new MutableLiveData<>();
    // 最近用户最大数量
    private final int MAX_RECENT_USERS = 5;

    /**
     * 构造方法，初始化UserRepository。
     */
    public LoginViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);
        loadRecentUsers();
    }

    public LiveData<Boolean> getLoginResult() { return loginResult; }
    public LiveData<String> getUsernameError() { return usernameError; }
    public LiveData<String> getPasswordError() { return passwordError; }
    public LiveData<String> getToastMessage() { return toastMessage; }
    public LiveData<ArrayList<String>> getRecentUsers() { return recentUsers; }
    public LiveData<String> getAutoFillPassword() { return autoFillPassword; }

    /**
     * 加载最近登录用户列表。
     */
    public void loadRecentUsers() {
        Log.d(TAG, "loadRecentUsers: 开始加载最近用户列表");
        SharedPreferences sp = getApplication().getSharedPreferences("user_info", Context.MODE_PRIVATE);
        Set<String> recent = sp.getStringSet("recent_users", new HashSet<>());
        Log.d(TAG, "loadRecentUsers: 加载到 " + recent.size() + " 个用户");
        recentUsers.setValue(new ArrayList<>(recent));
        Log.d(TAG, "loadRecentUsers: 最近用户列表加载完成");
    }

    /**
     * 用户名下拉选择时自动填充密码（带时效性检查）。
     */
    public void onUsernameSelected(String username) {
        Log.d(TAG, "onUsernameSelected: 用户选择用户名 " + username);
        SharedPreferences sp = getApplication().getSharedPreferences("user_info", Context.MODE_PRIVATE);
        boolean isRemembered = sp.getBoolean("remember_password", false);
        Log.d(TAG, "onUsernameSelected: 记住密码状态 " + isRemembered);
        if (isRemembered) {
            // 获取带时效性检查的密码
            String validPassword = PasswordTimeUtils.getValidPassword(sp, username);
            Log.d(TAG, "onUsernameSelected: 获取到有效密码 " + (validPassword != null && !validPassword.isEmpty()));
            autoFillPassword.setValue(validPassword);
            
            // 检查是否即将过期，给出提示
            if (PasswordTimeUtils.isPasswordExpiringSoon(sp, username)) {
                long remainingMinutes = PasswordTimeUtils.getRemainingTime(sp, username);
                Log.d(TAG, "onUsernameSelected: 密码即将过期，剩余时间 " + remainingMinutes + " 分钟");
                toastMessage.setValue("密码自动填充将在" + remainingMinutes + "分钟后过期");
            }
        }
    }

    /**
     * 登录方法，包含输入校验和异步数据库校验。
     * 支持MD5密码加密和带时效性的自动填充。
     * @param username 用户名
     * @param password 密码
     * @param rememberPassword 是否记住密码
     */
    public void login(String username, String password, boolean rememberPassword) {
        Log.d(TAG, "login: 开始登录流程，用户名=" + username + ", 记住密码=" + rememberPassword);
        // 防抖检查
        if (!DebounceUtils.canClick(String.valueOf(LOGIN_BUTTON_ID))) {
            Log.w(TAG, "login: 防抖检查失败，阻止重复点击");
            toastMessage.setValue("请稍后再试");
            return;
        }
        
        usernameError.setValue(null);
        passwordError.setValue(null);
        toastMessage.setValue(null);

        // 输入校验
        if (TextUtils.isEmpty(username)) {
            Log.d(TAG, "login: 用户名为空");
            usernameError.setValue("请输入用户名");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Log.d(TAG, "login: 密码为空");
            passwordError.setValue("请输入密码");
            return;
        }
        if (!isPasswordValid(password)) {
            Log.d(TAG, "login: 密码格式不正确");
            passwordError.setValue("密码至少8位，必须包含数字和字母");
            return;
        }
        
        Log.d(TAG, "login: 输入验证通过，开始设置防抖");
        // 设置防抖状态
        DebounceUtils.setDebounce(String.valueOf(LOGIN_BUTTON_ID), () -> {
            // 防抖结束后的回调，可以在这里做一些清理工作
            Log.d(TAG, "login: 防抖结束回调");
        });
        
        // 对密码进行MD5加密
        String encryptedPassword = MD5Utils.encrypt(password);
        Log.d(TAG, "login: 密码加密完成");
        
        // 异步数据库校验（使用加密后的密码）
        Log.d(TAG, "login: 开始数据库查询");
        userRepository.getUserByUsername(username, user -> {
            Log.d(TAG, "login: 数据库查询回调，用户存在=" + (user != null));
            if (user != null && encryptedPassword.equals(user.getPassword())) {
                Log.d(TAG, "login: 登录验证成功");
                updateRecentUsers(username);
                SharedPreferences sp = getApplication().getSharedPreferences("user_info", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                
                if (rememberPassword) {
                    editor.putBoolean("remember_password", true);
                    editor.putString("saved_username", username);
                    // 使用带时间戳的明文密码保存（用于自动填充）
                    PasswordTimeUtils.savePasswordWithTime(sp, username, password);
                } else {
                    editor.putBoolean("remember_password", false);
                    editor.remove("saved_username");
                    // 清除该用户的密码
                    PasswordTimeUtils.clearPassword(sp, username);
                }
                editor.putString("current_user", username);
                editor.apply();
                
                toastMessage.postValue("登录成功");
                loginResult.postValue(true);
                // 修改: 登录成功后清除防抖状态
                DebounceUtils.clearDebounce(String.valueOf(LOGIN_BUTTON_ID));
                Log.d(TAG, "login: 登录流程完成，登录成功");
            } else {
                Log.d(TAG, "login: 登录验证失败，用户名或密码错误");
                toastMessage.postValue("用户名或密码错误");
                loginResult.postValue(false);
                // 登录失败时清除防抖状态，允许用户重新尝试
                DebounceUtils.clearDebounce(String.valueOf(LOGIN_BUTTON_ID));
                Log.d(TAG, "login: 登录流程完成，登录失败");
            }
        });
    }

    /**
     * 密码格式校验，要求至少8位且包含字母和数字。
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
     * 更新最近登录用户列表。
     * @param username 当前登录成功的用户名
     */
    private void updateRecentUsers(String username) {
        SharedPreferences sp = getApplication().getSharedPreferences("user_info", Context.MODE_PRIVATE);
        Set<String> recentUsersSet = new HashSet<>(sp.getStringSet("recent_users", new HashSet<>())) ;
        recentUsersSet.remove(username);
        recentUsersSet.add(username);
        if (recentUsersSet.size() > MAX_RECENT_USERS) {
            ArrayList<String> userList = new ArrayList<>(recentUsersSet);
            userList.remove(0);
            recentUsersSet = new HashSet<>(userList);
        }
        sp.edit().putStringSet("recent_users", recentUsersSet).apply();
        recentUsers.postValue(new ArrayList<>(recentUsersSet));
    }
}
