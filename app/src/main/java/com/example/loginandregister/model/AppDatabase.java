package com.example.loginandregister.model;

import androidx.room.Database;
import androidx.room.RoomDatabase;

/**
 * Room数据库主入口，包含所有的表和DAO。
 * version用于数据库升级，entities声明所有表。
 */
@Database(entities = {User.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    /**
     * 获取用户表的DAO。
     */
    public abstract UserDao userDao();
} 