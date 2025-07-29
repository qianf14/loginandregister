package com.example.loginandregister.viewmodel;

import android.app.Application;
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

/**
 * 注册页面的ViewModel，负责注册业务逻辑、输入校验和与数据库的交互。
 * 通过LiveData将状态和错误信息通知UI。
 * 支持MD5密码加密存储。
 */
public class RegisterViewModel extends AndroidViewModel {
    private static final String TAG = "RegisterViewModel";
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
        Log.d(TAG, "register: 开始注册流程，用户名=" + username);

        // 防抖检查
        if (!DebounceUtils.canClick(String.valueOf(REGISTER_BUTTON_ID))) {
            Log.w(TAG, "register: 防抖检查失败，阻止重复点击");
            toastMessage.setValue("请稍后再试");
            return;
        }
        
        usernameError.setValue(null);
        passwordError.setValue(null);
        confirmPasswordError.setValue(null);
        toastMessage.setValue(null);

        // 输入校验
        if (TextUtils.isEmpty(username)) {
            Log.d(TAG, "register: 用户名为空");
            usernameError.setValue("请输入用户名");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Log.d(TAG, "register: 密码为空");
            passwordError.setValue("请输入密码");
            return;
        }
        if (TextUtils.isEmpty(confirmPassword)) {
            Log.d(TAG, "register: 确认密码为空");
            confirmPasswordError.setValue("请确认密码");
            return;
        }
        if (!isPasswordValid(password)) {
            Log.d(TAG, "register: 密码格式不正确");
            passwordError.setValue("密码至少8位，必须包含数字和字母");
            return;
        }
        if (!password.equals(confirmPassword)) {
            Log.d(TAG, "register: 两次输入的密码不一致");
            confirmPasswordError.setValue("两次输入的密码不一致");
            return;
        }

        Log.d(TAG, "register: 输入验证通过，开始设置防抖");
        
        // 设置防抖状态
        DebounceUtils.setDebounce(String.valueOf(REGISTER_BUTTON_ID), () -> {
            // 防抖结束后的回调，可以在这里做一些清理工作
            Log.d(TAG, "register: 防抖结束回调");
        });
        
        // 异步检查用户名唯一性
        Log.d(TAG, "register: 开始检查用户名唯一性");
        userRepository.getUserByUsername(username, user -> {
            Log.d(TAG, "register: 用户名检查回调，用户存在=" + (user != null));
            if (user != null) {
                Log.d(TAG, "register: 用户名已存在");
                usernameError.postValue("用户名已存在");
                // 注册失败时清除防抖状态，允许用户重新尝试
                DebounceUtils.clearDebounce(String.valueOf(REGISTER_BUTTON_ID));
            } else {
                // 对密码进行MD5加密
                String encryptedPassword = MD5Utils.encrypt(password);
                Log.d(TAG, "register: 密码加密完成");
                
                // 异步插入用户（存储加密后的密码）
                Log.d(TAG, "register: 开始插入用户到数据库");
                userRepository.registerUser(new User(username, encryptedPassword), id -> {
                    Log.d(TAG, "register: 用户插入回调，用户ID=" + id);
                    toastMessage.postValue("注册成功");
                    registerResult.postValue(true);
                    // 注册成功后清除防抖状态
                    DebounceUtils.clearDebounce(String.valueOf(REGISTER_BUTTON_ID));
                    Log.d(TAG, "register: 注册流程完成，注册成功");
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
