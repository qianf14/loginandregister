package com.example.loginandregister.utils;

import android.util.Log;
import java.security.MessageDigest;

/**
 * MD5加密工具类，用于密码加密。
 */
public class MD5Utils {
    private static final String TAG = "MD5Utils";
    
    /**
     * 对字符串进行MD5加密
     * @param input 要加密的字符串
     * @return 加密后的字符串，失败返回空字符串
     */
    public static String encrypt(String input) {
        Log.d(TAG, "encrypt: 开始MD5加密，输入长度=" + (input != null ? input.length() : 0));
        if (input == null || input.isEmpty()) {
            Log.w(TAG, "encrypt: 输入为空，返回空字符串");
            return "";
        }
        
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : messageDigest) {
                sb.append(String.format("%02x", b));
            }
            String result = sb.toString();
            Log.d(TAG, "encrypt: MD5加密完成，结果长度=" + result.length());
            return result;
        } catch (Exception e) {
            Log.e(TAG, "encrypt: MD5加密失败", e);
            e.printStackTrace();
            return "";
        }
    }
}
