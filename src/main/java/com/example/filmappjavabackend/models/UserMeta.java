package com.example.filmappjavabackend.models;


import java.util.ArrayList;
import java.util.List;

public class UserMeta {

    private List<String> liked;

    private List<String> disliked;

    public UserMeta() {
        this.liked = new ArrayList<>();
        this.disliked = new ArrayList<>();
    }

    public List<String> getLiked() {
        return liked;
    }

    public void setLiked(List<String> liked) {
        this.liked = liked;
    }

    public List<String> getDisliked() {
        return disliked;
    }

    public void setDisliked(List<String> disliked) {
        this.disliked = disliked;
    }
}
