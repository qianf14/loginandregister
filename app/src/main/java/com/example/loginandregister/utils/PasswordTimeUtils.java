package com.example.loginandregister.utils;

import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

/**
 * 密码时效性工具类，用于管理带时间戳的密码存储和验证。
 */
public class PasswordTimeUtils {
    private static final String TAG = "PasswordTimeUtils";
    // 密码有效期：24小时（毫秒）
    private static final long VALID_DURATION = 24 * 60 * 60 * 1000;
    
    /**
     * 保存带时间戳的密码
     * @param sp SharedPreferences实例
     * @param username 用户名
     * @param password 明文密码
     */
    public static void savePasswordWithTime(SharedPreferences sp, String username, String password) {
        Log.d(TAG, "savePasswordWithTime: 保存带时间戳的密码，用户名=" + username + ", 密码长度=" + (password != null ? password.length() : 0));
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Log.w(TAG, "savePasswordWithTime: 用户名或密码为空，忽略保存");
            return;
        }
        
        long currentTime = System.currentTimeMillis();
        String passwordWithTime = password + "_" + currentTime;
        sp.edit().putString(username + "_password_plain", passwordWithTime).apply();
        Log.d(TAG, "savePasswordWithTime: 密码保存完成，时间戳=" + currentTime);
    }
    
    /**
     * 获取有效的密码（检查时效性）
     * @param sp SharedPreferences实例
     * @param username 用户名
     * @return 有效的明文密码，过期或不存在返回空字符串
     */
    public static String getValidPassword(SharedPreferences sp, String username) {
        Log.d(TAG, "getValidPassword: 获取有效密码，用户名=" + username);
        if (TextUtils.isEmpty(username)) {
            Log.w(TAG, "getValidPassword: 用户名为空，返回空字符串");
            return "";
        }
        
        String passwordWithTime = sp.getString(username + "_password_plain", "");
        if (TextUtils.isEmpty(passwordWithTime)) {
            Log.d(TAG, "getValidPassword: 未找到保存的密码，返回空字符串");
            return "";
        }
        
        // 解析密码和时间戳
        String[] parts = passwordWithTime.split("_");
        if (parts.length != 2) {
            Log.w(TAG, "getValidPassword: 密码格式错误，清除无效数据");
            // 格式错误，清除无效数据
            sp.edit().remove(username + "_password_plain").apply();
            return "";
        }
        
        String password = parts[0];
        long savedTime;
        try {
            savedTime = Long.parseLong(parts[1]);
        } catch (NumberFormatException e) {
            Log.w(TAG, "getValidPassword: 时间戳格式错误，清除无效数据", e);
            // 时间戳格式错误，清除无效数据
            sp.edit().remove(username + "_password_plain").apply();
            return "";
        }
        
        long currentTime = System.currentTimeMillis();
        
        // 检查是否过期
        if (currentTime - savedTime > VALID_DURATION) {
            Log.d(TAG, "getValidPassword: 密码已过期，清除数据");
            // 过期，清除数据
            sp.edit().remove(username + "_password_plain").apply();
            return "";
        }
        
        Log.d(TAG, "getValidPassword: 返回有效密码，密码长度=" + password.length());
        return password;
    }
    
    /**
     * 获取密码剩余有效时间（分钟）
     * @param sp SharedPreferences实例
     * @param username 用户名
     * @return 剩余有效时间（分钟），过期返回0
     */
    public static long getRemainingTime(SharedPreferences sp, String username) {
        Log.d(TAG, "getRemainingTime: 获取密码剩余有效时间，用户名=" + username);
        if (TextUtils.isEmpty(username)) {
            Log.w(TAG, "getRemainingTime: 用户名为空，返回0");
            return 0;
        }
        
        String passwordWithTime = sp.getString(username + "_password_plain", "");
        if (TextUtils.isEmpty(passwordWithTime)) {
            Log.d(TAG, "getRemainingTime: 未找到保存的密码，返回0");
            return 0;
        }
        
        String[] parts = passwordWithTime.split("_");
        if (parts.length != 2) {
            Log.w(TAG, "getRemainingTime: 密码格式错误，返回0");
            return 0;
        }
        
        long savedTime;
        try {
            savedTime = Long.parseLong(parts[1]);
        } catch (NumberFormatException e) {
            Log.w(TAG, "getRemainingTime: 时间戳格式错误，返回0", e);
            return 0;
        }
        
        long currentTime = System.currentTimeMillis();
        long remainingTime = VALID_DURATION - (currentTime - savedTime);
        long remainingMinutes = Math.max(0, remainingTime / (60 * 1000)); // 返回分钟数
        
        Log.d(TAG, "getRemainingTime: 剩余时间=" + remainingMinutes + "分钟");
        return remainingMinutes;
    }
    
    /**
     * 检查密码是否即将过期（1小时内）
     * @param sp SharedPreferences实例
     * @param username 用户名
     * @return 是否即将过期
     */
    public static boolean isPasswordExpiringSoon(SharedPreferences sp, String username) {
        Log.d(TAG, "isPasswordExpiringSoon: 检查密码是否即将过期，用户名=" + username);
        long remainingTime = getRemainingTime(sp, username);
        boolean expiringSoon = remainingTime > 0 && remainingTime <= 60; // 1小时内过期
        Log.d(TAG, "isPasswordExpiringSoon: 密码即将过期=" + expiringSoon + ", 剩余时间=" + remainingTime + "分钟");
        return expiringSoon;
    }
    
    /**
     * 清除指定用户的密码
     * @param sp SharedPreferences实例
     * @param username 用户名
     */
    public static void clearPassword(SharedPreferences sp, String username) {
        Log.d(TAG, "clearPassword: 清除用户密码，用户名=" + username);
        if (!TextUtils.isEmpty(username)) {
            sp.edit().remove(username + "_password_plain").apply();
            Log.d(TAG, "clearPassword: 用户密码已清除");
        } else {
            Log.w(TAG, "clearPassword: 用户名为空，忽略清除操作");
        }
    }
}
