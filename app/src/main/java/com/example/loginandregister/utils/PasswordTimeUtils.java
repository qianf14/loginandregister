package com.example.loginandregister.utils;

import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 * 密码时效性工具类，用于管理带时间戳的密码存储和验证。
 */
public class PasswordTimeUtils {
    // 密码有效期：24小时（毫秒）
    private static final long VALID_DURATION = 24 * 60 * 60 * 1000;
    
    /**
     * 保存带时间戳的密码
     * @param sp SharedPreferences实例
     * @param username 用户名
     * @param password 明文密码
     */
    public static void savePasswordWithTime(SharedPreferences sp, String username, String password) {
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            return;
        }
        
        long currentTime = System.currentTimeMillis();
        String passwordWithTime = password + "_" + currentTime;
        sp.edit().putString(username + "_password_plain", passwordWithTime).apply();
    }
    
    /**
     * 获取有效的密码（检查时效性）
     * @param sp SharedPreferences实例
     * @param username 用户名
     * @return 有效的明文密码，过期或不存在返回空字符串
     */
    public static String getValidPassword(SharedPreferences sp, String username) {
        if (TextUtils.isEmpty(username)) {
            return "";
        }
        
        String passwordWithTime = sp.getString(username + "_password_plain", "");
        if (TextUtils.isEmpty(passwordWithTime)) {
            return "";
        }
        
        // 解析密码和时间戳
        String[] parts = passwordWithTime.split("_");
        if (parts.length != 2) {
            // 格式错误，清除无效数据
            sp.edit().remove(username + "_password_plain").apply();
            return "";
        }
        
        String password = parts[0];
        long savedTime;
        try {
            savedTime = Long.parseLong(parts[1]);
        } catch (NumberFormatException e) {
            // 时间戳格式错误，清除无效数据
            sp.edit().remove(username + "_password_plain").apply();
            return "";
        }
        
        long currentTime = System.currentTimeMillis();
        
        // 检查是否过期
        if (currentTime - savedTime > VALID_DURATION) {
            // 过期，清除数据
            sp.edit().remove(username + "_password_plain").apply();
            return "";
        }
        
        return password;
    }
    
    /**
     * 获取密码剩余有效时间（分钟）
     * @param sp SharedPreferences实例
     * @param username 用户名
     * @return 剩余有效时间（分钟），过期返回0
     */
    public static long getRemainingTime(SharedPreferences sp, String username) {
        if (TextUtils.isEmpty(username)) {
            return 0;
        }
        
        String passwordWithTime = sp.getString(username + "_password_plain", "");
        if (TextUtils.isEmpty(passwordWithTime)) {
            return 0;
        }
        
        String[] parts = passwordWithTime.split("_");
        if (parts.length != 2) {
            return 0;
        }
        
        long savedTime;
        try {
            savedTime = Long.parseLong(parts[1]);
        } catch (NumberFormatException e) {
            return 0;
        }
        
        long currentTime = System.currentTimeMillis();
        long remainingTime = VALID_DURATION - (currentTime - savedTime);
        
        return Math.max(0, remainingTime / (60 * 1000)); // 返回分钟数
    }
    
    /**
     * 检查密码是否即将过期（1小时内）
     * @param sp SharedPreferences实例
     * @param username 用户名
     * @return 是否即将过期
     */
    public static boolean isPasswordExpiringSoon(SharedPreferences sp, String username) {
        long remainingTime = getRemainingTime(sp, username);
        return remainingTime > 0 && remainingTime <= 60; // 1小时内过期
    }
    
    /**
     * 清除指定用户的密码
     * @param sp SharedPreferences实例
     * @param username 用户名
     */
    public static void clearPassword(SharedPreferences sp, String username) {
        if (!TextUtils.isEmpty(username)) {
            sp.edit().remove(username + "_password_plain").apply();
        }
    }
} 