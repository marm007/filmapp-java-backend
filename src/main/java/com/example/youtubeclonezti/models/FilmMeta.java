package com.example.youtubeclonezti.models;

public class FilmMeta {

    private int views;

    private int likes;

    private int dislikes;

    public FilmMeta() {
    }

    public FilmMeta(int views, int likes, int dislikes) {
        this.views = views;
        this.likes = likes;
        this.dislikes = dislikes;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getDislikes() {
        return dislikes;
    }

    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }
}
