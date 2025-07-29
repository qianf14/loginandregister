package com.example.loginandregister.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginandregister.R;
import com.example.loginandregister.model.Movie;

import java.util.ArrayList;
import java.util.List;

/**
 * 电影列表适配器，用于在RecyclerView中展示电影信息
 */
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    private List<Movie> movies;

    public MovieAdapter() {
        this.movies = new ArrayList<>();
    }

    public MovieAdapter(List<Movie> movies) {
        this.movies = movies != null ? movies : new ArrayList<>();
    }

    /**
     * 更新电影列表数据
     *
     * @param movies 新的电影列表
     */
    public void setMovies(List<Movie> movies) {
        this.movies = movies != null ? movies : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_movie, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        if (position >= 0 && position < movies.size()) {
            Movie movie = movies.get(position);
            holder.bind(movie);
        }
    }

    @Override
    public int getItemCount() {
        return movies != null ? movies.size() : 0;
    }

    /**
     * 电影ViewHolder，用于持有电影列表项的视图
     */
    static class MovieViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle;
        private TextView tvYear;
        private TextView tvRating;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvYear = itemView.findViewById(R.id.tvYear);
            tvRating = itemView.findViewById(R.id.tvRating);
        }

        /**
         * 绑定电影数据到视图
         *
         * @param movie 电影对象
         */
        public void bind(Movie movie) {
            if (movie != null) {
                tvTitle.setText(movie.getTitle());
                tvYear.setText(String.valueOf(movie.getYear()));
                tvRating.setText(String.format("%.1f", movie.getRating()));
            }
        }
    }
}
