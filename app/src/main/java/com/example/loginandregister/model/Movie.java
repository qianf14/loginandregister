package com.example.loginandregister.model;

/**
 * 电影实体类，用于表示电影信息
 */
public class Movie {
    private String title;  // 电影标题
    private int year;      // 上映年份
    private double rating; // 评分

    public Movie() {
        // 无参构造函数
    }

    public Movie(String title, int year, double rating) {
        this.title = title;
        this.year = year;
        this.rating = rating;
    }

    // Getter和Setter方法
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "title='" + title + '\'' +
                ", year=" + year +
                ", rating=" + rating +
                '}';
    }
}
