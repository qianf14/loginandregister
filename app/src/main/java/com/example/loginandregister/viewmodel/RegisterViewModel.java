package com.example.loginandregister.viewmodel;

import android.app.Application;
import android.text.TextUtils;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.loginandregister.model.User;
import com.example.loginandregister.repository.UserRepository;
import com.example.loginandregister.utils.DebounceUtils;
import com.example.loginandregister.utils.MD5Utils;

/**
 * 注册页面的ViewModel，负责注册业务逻辑、输入校验和与数据库的交互。
 * 通过LiveData将状态和错误信息通知UI。
 * 支持MD5密码加密存储。
 */
public class RegisterViewModel extends AndroidViewModel {
    private static final int REGISTER_BUTTON_ID = 3;
    // 注册结果，true表示注册成功
    private final MutableLiveData<Boolean> registerResult = new MutableLiveData<>();
    // 用户名输入错误提示
    private final MutableLiveData<String> usernameError = new MutableLiveData<>();
    // 密码输入错误提示
    private final MutableLiveData<String> passwordError = new MutableLiveData<>();
    // 确认密码输入错误提示
    private final MutableLiveData<String> confirmPasswordError = new MutableLiveData<>();
    // Toast消息提示
    private final MutableLiveData<String> toastMessage = new MutableLiveData<>();
    // 用户数据仓库
    private final UserRepository userRepository;

    /**
     * 构造方法，初始化UserRepository。
     */
    public RegisterViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);
    }

    public LiveData<Boolean> getRegisterResult() { return registerResult; }
    public LiveData<String> getUsernameError() { return usernameError; }
    public LiveData<String> getPasswordError() { return passwordError; }
    public LiveData<String> getConfirmPasswordError() { return confirmPasswordError; }
    public LiveData<String> getToastMessage() { return toastMessage; }

    /**
     * 注册方法，包含输入校验和异步数据库操作。
     * 密码使用MD5加密后存储到数据库。
     * @param username 用户名
     * @param password 密码
     * @param confirmPassword 确认密码
     */
    public void register(String username, String password, String confirmPassword) {
        // 防抖检查
        if (!DebounceUtils.canClick(String.valueOf(REGISTER_BUTTON_ID))) {
            toastMessage.setValue("请稍后再试");
            return;
        }
        
        usernameError.setValue(null);
        passwordError.setValue(null);
        confirmPasswordError.setValue(null);
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
        
        // 设置防抖状态
        DebounceUtils.setDebounce(String.valueOf(REGISTER_BUTTON_ID), () -> {
            // 防抖结束后的回调，可以在这里做一些清理工作
        });
        
        // 异步检查用户名唯一性
        userRepository.getUserByUsername(username, user -> {
            if (user != null) {
                usernameError.postValue("用户名已存在");
                // 注册失败时清除防抖状态，允许用户重新尝试
                DebounceUtils.clearDebounce(String.valueOf(REGISTER_BUTTON_ID));
            } else {
                // 对密码进行MD5加密
                String encryptedPassword = MD5Utils.encrypt(password);
                
                // 异步插入用户（存储加密后的密码）
                userRepository.registerUser(new User(username, encryptedPassword), id -> {
                    toastMessage.postValue("注册成功");
                    registerResult.postValue(true);
                    // 注册成功后清除防抖状态
                    DebounceUtils.clearDebounce(String.valueOf(REGISTER_BUTTON_ID));
                });
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
} 