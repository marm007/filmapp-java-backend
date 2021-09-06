package com.example.filmappjavabackend.models.updates;

import java.util.List;

public class PlaylistUpdate {

    private String title;

    private List<String> films;

    private boolean removeFilms = false;

    private boolean isPublic = true;

    public PlaylistUpdate() {
    }

    public List<String> getFilms() {
        return films;
    }

    public void setFilms(List<String> films) {
        this.films = films;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isRemoveFilms() {
        return removeFilms;
    }

    public void setRemoveFilms(boolean removeFilms) {
        this.removeFilms = removeFilms;
    }

    public boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }
}
