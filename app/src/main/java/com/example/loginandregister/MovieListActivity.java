package com.example.loginandregister;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginandregister.adapter.MovieAdapter;
import com.example.loginandregister.model.Movie;
import com.example.loginandregister.utils.JsonUtils;

import java.util.List;

/**
 * 电影列表Activity，用于展示从JSON文件解析的电影数据
 */
public class MovieListActivity extends AppCompatActivity {
    private static final String TAG = "MovieListActivity";
    
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private MovieAdapter movieAdapter;
    private List<Movie> movieList;
    private boolean isAscending = false; // 默认降序排列

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        
        Log.d(TAG, "onCreate: 电影列表页面创建");
        
        // 设置Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        // 设置标题栏
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("电影列表");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        // 初始化视图
        initViews();
        
        // 加载电影数据
        loadMovies();
    }
    
    /**
     * 初始化视图组件
     */
    private void initViews() {
        Log.d(TAG, "initViews: 初始化视图组件");
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        movieAdapter = new MovieAdapter();
        recyclerView.setAdapter(movieAdapter);
    }
    
    /**
     * 从JSON文件加载电影数据
     */
    private void loadMovies() {
        Log.d(TAG, "loadMovies: 开始加载电影数据");
        movieList = JsonUtils.loadMoviesFromAssets(this);
        
        if (movieList != null && !movieList.isEmpty()) {
            // 默认按评分降序排列
            movieList = JsonUtils.sortMoviesByRating(movieList, isAscending);
            movieAdapter.setMovies(movieList);
            Log.d(TAG, "loadMovies: 电影数据加载完成，数量=" + movieList.size());
        } else {
            Log.w(TAG, "loadMovies: 未加载到电影数据");
            Toast.makeText(this, "未找到电影数据", Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * 刷新电影列表显示
     */
    private void refreshMovieList() {
        Log.d(TAG, "refreshMovieList: 刷新电影列表显示");
        if (movieList != null) {
            List<Movie> sortedMovies = JsonUtils.sortMoviesByRating(movieList, isAscending);
            movieAdapter.setMovies(sortedMovies);
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu: 创建菜单");
        getMenuInflater().inflate(R.menu.menu_movie_list, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: 菜单项选择，itemId=" + item.getItemId());
        int itemId = item.getItemId();
        
        if (itemId == android.R.id.home) {
            // 返回按钮
            finish();
            return true;
        } else if (itemId == R.id.action_sort_asc) {
            // 按评分升序排列
            isAscending = true;
            refreshMovieList();
            Toast.makeText(this, "按评分升序排列", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.action_sort_desc) {
            // 按评分降序排列
            isAscending = false;
            refreshMovieList();
            Toast.makeText(this, "按评分降序排列", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
