package com.example.loginandregister.viewmodel;

import android.app.Application;
import android.text.TextUtils;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.loginandregister.model.User;
import com.example.loginandregister.repository.UserRepository;

public class RegisterViewModel extends AndroidViewModel {
    private final MutableLiveData<Boolean> registerResult = new MutableLiveData<>();
    private final MutableLiveData<String> usernameError = new MutableLiveData<>();
    private final MutableLiveData<String> passwordError = new MutableLiveData<>();
    private final MutableLiveData<String> confirmPasswordError = new MutableLiveData<>();
    private final MutableLiveData<String> toastMessage = new MutableLiveData<>();
    private final UserRepository userRepository;

    public RegisterViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);
    }

    public LiveData<Boolean> getRegisterResult() { return registerResult; }
    public LiveData<String> getUsernameError() { return usernameError; }
    public LiveData<String> getPasswordError() { return passwordError; }
    public LiveData<String> getConfirmPasswordError() { return confirmPasswordError; }
    public LiveData<String> getToastMessage() { return toastMessage; }

    public void register(String username, String password, String confirmPassword) {
        usernameError.setValue(null);
        passwordError.setValue(null);
        confirmPasswordError.setValue(null);
        toastMessage.setValue(null);

        if (TextUtils.isEmpty(username)) {
            usernameError.setValue("请输入用户名");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            passwordError.setValue("请输入密码");
            return;
        }
        if (TextUtils.isEmpty(confirmPassword)) {
            confirmPasswordError.setValue("请确认密码");
            return;
        }
        if (!isPasswordValid(password)) {
            passwordError.setValue("密码至少8位，必须包含数字和字母");
            return;
        }
        if (!password.equals(confirmPassword)) {
            confirmPasswordError.setValue("两次输入的密码不一致");
            return;
        }
        // 异步检查用户名唯一性
        userRepository.getUserByUsername(username, user -> {
            if (user != null) {
                usernameError.postValue("用户名已存在");
            } else {
                // 异步插入用户
                userRepository.registerUser(new User(username, password), id -> {
                    toastMessage.postValue("注册成功");
                    registerResult.postValue(true);
                });
            }
        });
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
} 