package com.example.loginandregister.utils;

import android.content.Context;
import android.util.Log;

import com.example.loginandregister.model.Movie;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * JSON工具类，用于读取和解析assets目录下的JSON文件
 */
public class JsonUtils {
    private static final String TAG = "JsonUtils";

    /**
     * 从assets目录读取movies.json文件并解析为Movie对象列表
     *
     * @param context 应用上下文
     * @return Movie对象列表
     */
    public static List<Movie> loadMoviesFromAssets(Context context) {
        Log.d(TAG, "loadMoviesFromAssets: 开始加载电影数据");
        String json = null;
        try {
            // 从assets目录读取movies.json文件
            InputStream is = context.getAssets().open("movies.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
            Log.d(TAG, "loadMoviesFromAssets: JSON文件读取成功，大小=" + size + "字节");
        } catch (IOException ex) {
            Log.e(TAG, "loadMoviesFromAssets: 读取JSON文件失败", ex);
            ex.printStackTrace();
            return new ArrayList<>();
        }

        // 使用Gson解析JSON为Movie对象列表
        try {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Movie>>() {
            }.getType();
            List<Movie> movies = gson.fromJson(json, listType);
            Log.d(TAG, "loadMoviesFromAssets: JSON解析成功，电影数量=" + movies.size());
            return movies;
        } catch (Exception e) {
            Log.e(TAG, "loadMoviesFromAssets: JSON解析失败", e);
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * 按评分对电影列表进行排序
     *
     * @param movies  电影列表
     * @param ascending 是否升序排列
     * @return 排序后的电影列表
     */
    public static List<Movie> sortMoviesByRating(List<Movie> movies, boolean ascending) {
        Log.d(TAG, "sortMoviesByRating: 开始按评分排序，升序=" + ascending);
        if (movies == null || movies.isEmpty()) {
            Log.w(TAG, "sortMoviesByRating: 电影列表为空，返回原列表");
            return movies;
        }

        // 创建新列表以避免修改原始列表
        List<Movie> sortedMovies = new ArrayList<>(movies);
        
        if (ascending) {
            // 按评分升序排列
            Collections.sort(sortedMovies, (m1, m2) -> Double.compare(m1.getRating(), m2.getRating()));
        } else {
            // 按评分降序排列
            Collections.sort(sortedMovies, (m1, m2) -> Double.compare(m2.getRating(), m1.getRating()));
        }
        
        Log.d(TAG, "sortMoviesByRating: 排序完成，电影数量=" + sortedMovies.size());
        return sortedMovies;
    }
}
