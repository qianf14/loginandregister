package com.example.loginandregister.repository;

import android.content.Context;
import android.util.Log;
import androidx.room.Room;
import com.example.loginandregister.model.AppDatabase;
import com.example.loginandregister.model.User;
import com.example.loginandregister.model.UserDao;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 用户数据仓库，封装所有与用户相关的数据库操作。
 * 通过异步线程池避免主线程阻塞。
 */
public class UserRepository {
    private static final String TAG = "UserRepository";
    private final AppDatabase db;
    private final UserDao userDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    /**
     * 构造方法，初始化Room数据库和DAO。
     * @param context 应用上下文
     */
    public UserRepository(Context context) {
        db = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "app_db").build();
        userDao = db.userDao();
    }

    /**
     * 异步注册用户。
     * @param user 用户对象
     * @param callback 注册结果回调，返回新用户id
     */
    public void registerUser(User user, Callback<Long> callback) {
        Log.d(TAG, "registerUser: 开始注册用户，用户名=" + user.getUsername());
        executor.execute(() -> {
            long id = userDao.insertUser(user);
            Log.d(TAG, "registerUser: 用户注册完成，用户ID=" + id);
            if (callback != null) callback.onResult(id);
        });
    }

    /**
     * 异步根据用户名查询用户。
     * @param username 用户名
     * @param callback 查询结果回调，返回User对象或null
     */
    public void getUserByUsername(String username, Callback<User> callback) {
        Log.d(TAG, "getUserByUsername: 开始查询用户，用户名=" + username);
        executor.execute(() -> {
            User user = userDao.getUserByUsername(username);
            Log.d(TAG, "getUserByUsername: 用户查询完成，用户存在=" + (user != null));
            if (callback != null) callback.onResult(user);
        });
    }

    /**
     * 通用回调接口。
     * @param <T> 返回类型
     */
    public interface Callback<T> {
        void onResult(T result);
    }
}
