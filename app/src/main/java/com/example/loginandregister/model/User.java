package com.example.loginandregister.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * 用户实体类，对应Room数据库中的users表。
 */
@Entity(tableName = "users")
public class User {
    /**
     * 用户主键，自增。
     */
    @PrimaryKey(autoGenerate = true)
    private int id;
    /**
     * 用户名，唯一。
     */
    private String username;
    /**
     * 密码。
     */
    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
} 