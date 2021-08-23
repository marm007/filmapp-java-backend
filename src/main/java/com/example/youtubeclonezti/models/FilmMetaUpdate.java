package com.example.youtubeclonezti.models;

public class FilmMetaUpdate {

    private boolean viewed;

    private ELike liked = ELike.none;

    public FilmMetaUpdate() {
    }

    public boolean isViewed() {
        return viewed;
    }

    public void setViewed(boolean viewed) {
        this.viewed = viewed;
    }

    public ELike getLiked() {
        return liked;
    }

    public void setLiked(ELike liked) {
        this.liked = liked;
    }
}
