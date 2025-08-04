package com.example.loginandregister.viewmodel.movie;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.loginandregister.model.Movie;
import com.example.loginandregister.utils.JsonUtils;

import java.util.List;

/**
 * 电影列表ViewModel，用于管理电影列表页面的数据逻辑
 */
public class MovieListViewModel extends ViewModel {
    private static final String TAG = "MovieListViewModel";
    
    private MutableLiveData<List<Movie>> moviesLiveData = new MutableLiveData<>();
    private MutableLiveData<String> toastMessage = new MutableLiveData<>();
    private MutableLiveData<Boolean> loadingLiveData = new MutableLiveData<>(); // 加载状态
    private boolean isAscending = false; // 默认降序排列
    private List<Movie> movieList; // 原始电影列表
    
    /**
     * 从JSON文件加载电影数据
     *
     * @param context 应用上下文
     */
    public void loadMovies(Context context) {
        Log.d(TAG, "loadMovies: 开始加载电影数据");
        loadingLiveData.setValue(true); // 设置加载状态为true
        
        // 使用异步方法加载电影数据
        JsonUtils.loadMoviesFromAssetsAsync(context, new JsonUtils.LoadMoviesCallback() {
            @Override
            public void onSuccess(List<Movie> movies) {
                Log.d(TAG, "loadMovies: 电影数据加载成功，数量=" + (movies != null ? movies.size() : 0));
                loadingLiveData.postValue(false); // 设置加载状态为false
                movieList = movies;
                
                if (movieList != null && !movieList.isEmpty()) {
                    // 默认按评分降序排列
                    List<Movie> sortedMovies = JsonUtils.sortMoviesByRating(movieList, isAscending);
                    moviesLiveData.postValue(sortedMovies);
                    Log.d(TAG, "loadMovies: 电影数据加载完成，数量=" + movieList.size());
                } else {
                    Log.w(TAG, "loadMovies: 未加载到电影数据");
                    toastMessage.postValue("未找到电影数据");
                }
            }
            
            @Override
            public void onError(Exception error) {
                Log.e(TAG, "loadMovies: 电影数据加载失败", error);
                loadingLiveData.postValue(false); // 设置加载状态为false
                toastMessage.postValue("数据加载失败: " + error.getMessage());
            }
        });
    }
    
    /**
     * 按评分排序电影列表
     *
     * @param ascending 是否升序排列
     */
    public void sortMoviesByRating(boolean ascending) {
        isAscending = ascending;
        Log.d(TAG, "sortMoviesByRating: 开始按评分排序，升序=" + ascending);
        if (movieList != null && !movieList.isEmpty()) {
            List<Movie> sortedMovies = JsonUtils.sortMoviesByRating(movieList, isAscending);
            moviesLiveData.setValue(sortedMovies);
            Log.d(TAG, "sortMoviesByRating: 排序完成，电影数量=" + sortedMovies.size());
        }
    }
    
    /**
     * 获取电影列表的LiveData
     *
     * @return 电影列表的LiveData
     */
    public LiveData<List<Movie>> getMovies() {
        return moviesLiveData;
    }
    
    /**
     * 获取Toast消息的LiveData
     *
     * @return Toast消息的LiveData
     */
    public LiveData<String> getToastMessage() {
        return toastMessage;
    }
    
    /**
     * 获取加载状态的LiveData
     *
     * @return 加载状态的LiveData
     */
    public LiveData<Boolean> getLoading() {
        return loadingLiveData;
    }
}
