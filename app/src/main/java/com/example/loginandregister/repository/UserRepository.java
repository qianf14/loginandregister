package com.example.loginandregister.repository;

import com.example.loginandregister.model.User;

public class UserRepository {
    // 这里可以扩展为数据库或网络请求，目前仅做本地校验示例
    public boolean login(String username, String password) {
        // 示例：用户名和密码都为admin时登录成功
        return "admin".equals(username) && "admin".equals(password);
    }
} 