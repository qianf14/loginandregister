package com.example.loginandregister.ui.movie;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginandregister.R;
import com.example.loginandregister.adapter.MovieAdapter;
import com.example.loginandregister.model.Movie;
import com.example.loginandregister.viewmodel.movie.MovieListViewModel;

import java.util.List;

/**
 * 电影列表Activity，用于展示从JSON文件解析的电影数据
 */
public class MovieListActivity extends AppCompatActivity {
    private static final String TAG = "MovieListActivity";
    
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private MovieAdapter movieAdapter;
    private MovieListViewModel movieListViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        
        Log.d(TAG, "onCreate: 电影列表页面创建");
        
        // 初始化ViewModel
        movieListViewModel = new ViewModelProvider(this).get(MovieListViewModel.class);
        
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
        
        // 观察电影数据变化
        movieListViewModel.getMovies().observe(this, movies -> {
            if (movies != null) {
                movieAdapter.setMovies(movies);
            }
        });
        
        // 观察Toast消息
        movieListViewModel.getToastMessage().observe(this, message -> {
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        });
        
        // 加载电影数据
        movieListViewModel.loadMovies(this);
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
            movieListViewModel.sortMoviesByRating(true);
            Toast.makeText(this, "按评分升序排列", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.action_sort_desc) {
            // 按评分降序排列
            movieListViewModel.sortMoviesByRating(false);
            Toast.makeText(this, "按评分降序排列", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
