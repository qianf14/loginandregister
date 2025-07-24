package com.example.loginandregister.viewmodel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.loginandregister.model.User;
import com.example.loginandregister.repository.UserRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * 登录页面的ViewModel，负责登录业务逻辑、输入校验和与数据库的交互。
 * 通过LiveData将状态和错误信息通知UI。
 */
public class LoginViewModel extends AndroidViewModel {
    // 用户数据仓库
    private final UserRepository userRepository;
    // 登录结果，true表示登录成功
    private final MutableLiveData<Boolean> loginResult = new MutableLiveData<>();
    // 用户名输入错误提示
    private final MutableLiveData<String> usernameError = new MutableLiveData<>();
    // 密码输入错误提示
    private final MutableLiveData<String> passwordError = new MutableLiveData<>();
    // Toast消息提示
    private MutableLiveData<String> toastMessage = new MutableLiveData<>();
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
        SharedPreferences sp = getApplication().getSharedPreferences("user_info", Context.MODE_PRIVATE);
        Set<String> recent = sp.getStringSet("recent_users", new HashSet<>());
        recentUsers.setValue(new ArrayList<>(recent));
    }

    /**
     * 用户名下拉选择时自动填充密码。
     */
    public void onUsernameSelected(String username) {
        SharedPreferences sp = getApplication().getSharedPreferences("user_info", Context.MODE_PRIVATE);
        boolean isRemembered = sp.getBoolean("remember_password", false);
        if (isRemembered) {
            String savedPassword = sp.getString(username + "_password", "");
            autoFillPassword.setValue(savedPassword);
        }
    }

    /**
     * 登录方法，包含输入校验和异步数据库校验。
     * @param username 用户名
     * @param password 密码
     * @param rememberPassword 是否记住密码
     */
    public void login(String username, String password, boolean rememberPassword) {
        usernameError.setValue(null);
        passwordError.setValue(null);
        toastMessage.setValue(null);

        // 输入校验
        if (TextUtils.isEmpty(username)) {
            usernameError.setValue("请输入用户名");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            passwordError.setValue("请输入密码");
            return;
        }
        if (!isPasswordValid(password)) {
            passwordError.setValue("密码至少8位，必须包含数字和字母");
            return;
        }
        // 异步数据库校验
        userRepository.getUserByUsername(username, user -> {
            if (user != null && password.equals(user.getPassword())) {
                updateRecentUsers(username);
                SharedPreferences sp = getApplication().getSharedPreferences("user_info", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                if (rememberPassword) {
                    editor.putBoolean("remember_password", true);
                    editor.putString("saved_username", username);
                    editor.putString("saved_password", password);
                    editor.putString(username + "_password", password);
                } else {
                    editor.putBoolean("remember_password", false);
                    editor.remove("saved_username");
                    editor.remove("saved_password");
                    editor.remove(username + "_password");
                }
                editor.putString("current_user", username);
                editor.apply();
                toastMessage.postValue("登录成功");
                loginResult.postValue(true);
            } else {
                toastMessage.postValue("用户名或密码错误");
                loginResult.postValue(false);
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