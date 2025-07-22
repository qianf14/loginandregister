package com.example.loginandregister.repository;

import android.content.Context;
import androidx.room.Room;
import com.example.loginandregister.model.AppDatabase;
import com.example.loginandregister.model.User;
import com.example.loginandregister.model.UserDao;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserRepository {
    private final AppDatabase db;
    private final UserDao userDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public UserRepository(Context context) {
        db = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "app_db").build();
        userDao = db.userDao();
    }

    // 异步注册用户
    public void registerUser(User user, Callback<Long> callback) {
        executor.execute(() -> {
            long id = userDao.insertUser(user);
            if (callback != null) callback.onResult(id);
        });
    }

    // 异步根据用户名查询用户
    public void getUserByUsername(String username, Callback<User> callback) {
        executor.execute(() -> {
            User user = userDao.getUserByUsername(username);
            if (callback != null) callback.onResult(user);
        });
    }

    public interface Callback<T> {
        void onResult(T result);
    }
} 