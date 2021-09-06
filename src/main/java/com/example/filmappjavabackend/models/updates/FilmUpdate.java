package com.example.filmappjavabackend.models.updates;

import javax.validation.constraints.Size;

public class FilmUpdate {

    @Size(min = 10, max = 150, message = "title must be at least 10 characters long")
    private String title;

    @Size(min = 10, max = 500, message = "description must be at least 10 characters long")
    private String description;

    public FilmUpdate() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
