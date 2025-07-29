package com.example.loginandregister.utils;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * 防抖工具类，用于防止用户快速重复点击按钮。
 * 支持多个按钮的独立防抖控制。
 */
public class DebounceUtils {
    private static final String TAG = "DebounceUtils";
    // 默认防抖延迟时间（毫秒）
    private static final long DEFAULT_DEBOUNCE_DELAY = 3000;
    
    // Handler用于延迟执行
    private static final Handler handler = new Handler(Looper.getMainLooper());
    
    // 存储每个按钮的防抖状态
    private static final Map<String, Boolean> debounceMap = new HashMap<>();
    
    // 存储每个按钮的Runnable，用于正确清除
    private static final Map<String, Runnable> runnableMap = new HashMap<>();
    
    /**
     * 检查按钮是否可以点击（防抖检查）
     * @param buttonId 按钮唯一标识
     * @return true表示可以点击，false表示正在防抖中
     */
    public static boolean canClick(String buttonId) {
        boolean canClick = !debounceMap.getOrDefault(buttonId, false);
        Log.d(TAG, "canClick: 按钮ID=" + buttonId + ", 可以点击=" + canClick);
        return canClick;
    }
    
    /**
     * 设置按钮防抖状态
     * @param buttonId 按钮唯一标识
     * @param delay 防抖延迟时间（毫秒）
     * @param callback 防抖结束后执行的回调
     */
    public static void setDebounce(String buttonId, long delay, Runnable callback) {
        Log.d(TAG, "setDebounce: 设置防抖，按钮ID=" + buttonId + ", 延迟=" + delay + "ms");
        // 如果已经在防抖中，直接返回
        if (isDebouncing(buttonId)) {
            Log.w(TAG, "setDebounce: 按钮已在防抖中，忽略重复设置，按钮ID=" + buttonId);
            return;
        }
        
        // 设置防抖状态为true
        debounceMap.put(buttonId, true);
        Log.d(TAG, "setDebounce: 防抖状态已设置为true，按钮ID=" + buttonId);
        
        // 创建Runnable用于延迟执行
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "setDebounce: 防抖结束，恢复可点击状态，按钮ID=" + buttonId);
                debounceMap.put(buttonId, false);
                runnableMap.remove(buttonId);
                if (callback != null) {
                    callback.run();
                }
            }
        };
        
        // 保存Runnable引用
        runnableMap.put(buttonId, runnable);
        
        // 延迟后恢复可点击状态
        handler.postDelayed(runnable, delay);
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
        Log.d(TAG, "clearDebounce: 清除防抖状态，按钮ID=" + buttonId);
        debounceMap.remove(buttonId);
        Runnable runnable = runnableMap.remove(buttonId);
        if (runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }
    
    /**
     * 清除所有防抖状态
     */
    public static void clearAllDebounce() {
        Log.d(TAG, "clearAllDebounce: 清除所有防抖状态");
        debounceMap.clear();
        for (Runnable runnable : runnableMap.values()) {
            handler.removeCallbacks(runnable);
        }
        runnableMap.clear();
    }
    
    /**
     * 获取按钮的防抖状态
     * @param buttonId 按钮唯一标识
     * @return true表示正在防抖中，false表示可以点击
     */
    public static boolean isDebouncing(String buttonId) {
        boolean isDebouncing = debounceMap.getOrDefault(buttonId, false);
        Log.d(TAG, "isDebouncing: 按钮ID=" + buttonId + ", 正在防抖中=" + isDebouncing);
        return isDebouncing;
    }
}
