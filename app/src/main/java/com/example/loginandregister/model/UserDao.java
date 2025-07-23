package com.example.loginandregister.model;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

/**
 * 用户数据访问对象（DAO），定义对users表的操作。
 */
@Dao
public interface UserDao {
    /**
     * 插入一个用户。
     * @param user 用户对象
     * @return 新插入用户的主键id
     */
    @Insert
    long insertUser(User user);

    /**
     * 根据用户名查询用户。
     * @param username 用户名
     * @return 查询到的用户对象，若不存在返回null
     */
    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    User getUserByUsername(String username);
} 