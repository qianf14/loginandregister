package com.example.loginandregister.utils;

import android.os.Handler;
import android.os.Looper;

import java.util.HashMap;
import java.util.Map;

/**
 * 防抖工具类，用于防止用户快速重复点击按钮。
 * 支持多个按钮的独立防抖控制。
 */
public class DebounceUtils {
    // 默认防抖延迟时间（毫秒）
    private static final long DEFAULT_DEBOUNCE_DELAY = 500;
    
    // Handler用于延迟执行
    private static final Handler handler = new Handler(Looper.getMainLooper());
    
    // 存储每个按钮的防抖状态
    private static final Map<String, Boolean> debounceMap = new HashMap<>();
    
    /**
     * 检查按钮是否可以点击（防抖检查）
     * @param buttonId 按钮唯一标识
     * @return true表示可以点击，false表示正在防抖中
     */
    public static boolean canClick(String buttonId) {
        return !debounceMap.getOrDefault(buttonId, false);
    }
    
    /**
     * 设置按钮防抖状态
     * @param buttonId 按钮唯一标识
     * @param delay 防抖延迟时间（毫秒）
     * @param callback 防抖结束后执行的回调
     */
    public static void setDebounce(String buttonId, long delay, Runnable callback) {
        // 设置防抖状态为true
        debounceMap.put(buttonId, true);
        
        // 延迟后恢复可点击状态
        handler.postDelayed(() -> {
            debounceMap.put(buttonId, false);
            if (callback != null) {
                callback.run();
            }
        }, delay);
    }
    
    /**
     * 使用默认延迟时间设置防抖
     * @param buttonId 按钮唯一标识
     * @param callback 防抖结束后执行的回调
     */
    public static void setDebounce(String buttonId, Runnable callback) {
        setDebounce(buttonId, DEFAULT_DEBOUNCE_DELAY, callback);
    }
    
    /**
     * 清除指定按钮的防抖状态
     * @param buttonId 按钮唯一标识
     */
    public static void clearDebounce(String buttonId) {
        debounceMap.remove(buttonId);
        handler.removeCallbacksAndMessages(buttonId);
    }
    
    /**
     * 清除所有防抖状态
     */
    public static void clearAllDebounce() {
        debounceMap.clear();
        handler.removeCallbacksAndMessages(null);
    }
    
    /**
     * 获取按钮的防抖状态
     * @param buttonId 按钮唯一标识
     * @return true表示正在防抖中，false表示可以点击
     */
    public static boolean isDebouncing(String buttonId) {
        return debounceMap.getOrDefault(buttonId, false);
    }
}
