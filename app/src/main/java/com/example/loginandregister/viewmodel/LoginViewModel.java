package com.example.loginandregister.viewmodel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.loginandregister.repository.UserRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class LoginViewModel extends AndroidViewModel {
    private final UserRepository userRepository = new UserRepository();
    private final MutableLiveData<Boolean> loginResult = new MutableLiveData<>();
    private final MutableLiveData<String> usernameError = new MutableLiveData<>();
    private final MutableLiveData<String> passwordError = new MutableLiveData<>();
    private final MutableLiveData<String> toastMessage = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<String>> recentUsers = new MutableLiveData<>();
    private final MutableLiveData<String> autoFillPassword = new MutableLiveData<>();
    private final int MAX_RECENT_USERS = 5;

    public LoginViewModel(@NonNull Application application) {
        super(application);
        loadRecentUsers();
    }

    public LiveData<Boolean> getLoginResult() { return loginResult; }
    public LiveData<String> getUsernameError() { return usernameError; }
    public LiveData<String> getPasswordError() { return passwordError; }
    public LiveData<String> getToastMessage() { return toastMessage; }
    public LiveData<ArrayList<String>> getRecentUsers() { return recentUsers; }
    public LiveData<String> getAutoFillPassword() { return autoFillPassword; }

    public void loadRecentUsers() {
        SharedPreferences sp = getApplication().getSharedPreferences("user_info", Context.MODE_PRIVATE);
        Set<String> recent = sp.getStringSet("recent_users", new HashSet<>());
        recentUsers.setValue(new ArrayList<>(recent));
    }

    public void onUsernameSelected(String username) {
        SharedPreferences sp = getApplication().getSharedPreferences("user_info", Context.MODE_PRIVATE);
        boolean isRemembered = sp.getBoolean("remember_password", false);
        if (isRemembered) {
            String savedPassword = sp.getString(username + "_password", "");
            autoFillPassword.setValue(savedPassword);
        }
    }

    public void login(String username, String password, boolean rememberPassword) {
        usernameError.setValue(null);
        passwordError.setValue(null);
        toastMessage.setValue(null);

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
        SharedPreferences sp = getApplication().getSharedPreferences("user_info", Context.MODE_PRIVATE);
        String savedPassword = sp.getString(username, "");
        if (savedPassword.equals(password)) {
            updateRecentUsers(username);
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
            toastMessage.setValue("登录成功");
            loginResult.setValue(true);
        } else {
            toastMessage.setValue("用户名或密码错误");
            loginResult.setValue(false);
        }
    }

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

    private void updateRecentUsers(String username) {
        SharedPreferences sp = getApplication().getSharedPreferences("user_info", Context.MODE_PRIVATE);
        Set<String> recentUsersSet = new HashSet<>(sp.getStringSet("recent_users", new HashSet<>()));
        recentUsersSet.remove(username);
        recentUsersSet.add(username);
        if (recentUsersSet.size() > MAX_RECENT_USERS) {
            ArrayList<String> userList = new ArrayList<>(recentUsersSet);
            userList.remove(0);
            recentUsersSet = new HashSet<>(userList);
        }
        sp.edit().putStringSet("recent_users", recentUsersSet).apply();
        recentUsers.setValue(new ArrayList<>(recentUsersSet));
    }
} 