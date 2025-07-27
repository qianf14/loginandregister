package com.example.loginandregister.utils;

import java.security.MessageDigest;

/**
 * MD5加密工具类，用于密码加密。
 */
public class MD5Utils {
    
    /**
     * 对字符串进行MD5加密
     * @param input 要加密的字符串
     * @return 加密后的字符串，失败返回空字符串
     */
    public static String encrypt(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : messageDigest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
} 