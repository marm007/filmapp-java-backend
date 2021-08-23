package com.example.youtubeclonezti.models;

import org.bson.types.Binary;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "thumbnails")
public class Thumbnail {

    @Id
    private String id;

    private Binary original;

    private Binary poster;

    private Binary thumbnail;

    private Binary preview;

    public Thumbnail() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Binary getOriginal() {
        return original;
    }

    public void setOriginal(Binary original) {
        this.original = original;
    }

    public Binary getPoster() {
        return poster;
    }

    public void setPoster(Binary poster) {
        this.poster = poster;
    }

    public Binary getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Binary thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Binary getPreview() {
        return preview;
    }

    public void setPreview(Binary preview) {
        this.preview = preview;
    }
}