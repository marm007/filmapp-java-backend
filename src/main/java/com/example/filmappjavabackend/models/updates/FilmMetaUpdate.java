package com.example.filmappjavabackend.models.updates;

import com.example.filmappjavabackend.models.enums.ELike;

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
