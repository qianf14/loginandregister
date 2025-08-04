package com.example.loginandregister.utils;

import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池管理工具类，用于统一管理应用中的所有子线程任务
 */
public class ThreadPoolUtils {
    private static final String TAG = "ThreadPoolUtils";
    
    // 单例实例
    private static volatile ThreadPoolUtils instance;
    
    // 线程池
    private ExecutorService executorService;
    
    // 私有构造函数
    private ThreadPoolUtils() {
        // 获取CPU核心数
        int cpuCount = Runtime.getRuntime().availableProcessors();
        // 核心线程数
        int corePoolSize = cpuCount + 1;
        // 最大线程数
        int maximumPoolSize = cpuCount * 2 + 1;
        // 线程空闲时间
        long keepAliveTime = 30L;
        
        Log.d(TAG, "ThreadPoolUtils: 初始化线程池，核心线程数=" + corePoolSize + 
              ", 最大线程数=" + maximumPoolSize);
        
        // 创建线程池
        executorService = new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(128) // 设置队列大小
        );
    }
    
    /**
     * 获取单例实例
     *
     * @return ThreadPoolUtils实例
     */
    public static ThreadPoolUtils getInstance() {
        if (instance == null) {
            synchronized (ThreadPoolUtils.class) {
                if (instance == null) {
                    instance = new ThreadPoolUtils();
                }
            }
        }
        return instance;
    }
    
    /**
     * 执行任务
     *
     * @param runnable 要执行的任务
     */
    public void execute(Runnable runnable) {
        if (executorService != null && !executorService.isShutdown()) {
            Log.d(TAG, "execute: 提交任务到线程池");
            executorService.execute(runnable);
        } else {
            Log.w(TAG, "execute: 线程池不可用，任务未执行");
        }
    }
    
    /**
     * 关闭线程池
     */
    public void shutdown() {
        if (executorService != null && !executorService.isShutdown()) {
            Log.d(TAG, "shutdown: 关闭线程池");
            executorService.shutdown();
        }
    }
    
    /**
     * 立即关闭线程池
     */
    public void shutdownNow() {
        if (executorService != null && !executorService.isShutdown()) {
            Log.d(TAG, "shutdownNow: 立即关闭线程池");
            executorService.shutdownNow();
        }
    }
    
    /**
     * 检查线程池是否已关闭
     *
     * @return true表示已关闭，false表示未关闭
     */
    public boolean isShutdown() {
        return executorService != null && executorService.isShutdown();
    }
}
