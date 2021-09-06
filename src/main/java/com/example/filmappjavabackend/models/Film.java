package com.example.filmappjavabackend.models;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Date;

@Document(collection = "films")
public class Film {
    @Id
    private ObjectId id;

    @NotBlank
    @Size(min = 10, max = 150, message = "The title must be between {min} and {max} characters long")
    @Indexed(unique = true)
    private String title;

    @NotBlank
    @Size(min = 10, max = 5000, message = "The description must be between {min} and {max} characters long")
    private String description;

    private FilmMeta meta;

    @NotEmpty
    private String thumbnail;

    private String film;

    private String url;

    private String authorID;

    private String authorUsername;

    @CreatedDate
    private Date createdDate;

    public Film(@NotBlank @Size(min = 10, max = 150, message =  "The title must be between {min} and {max} characters long") String title,
                @NotBlank @Size(min = 10, max = 5000,  message = "The description must be between {min} and {max} characters long") String description
               ) {
        this.title = title;
        this.description = description;
        this.meta = new FilmMeta(0, 0, 0);
    }

    private boolean isActive = true;

    public Film() {
        this.meta = new FilmMeta(0, 0, 0);
    }

    public String getId() {
        return id.toString();
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthorID() {
        return authorID;
    }

    public void setAuthorID(String authorID) {
        this.authorID = authorID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public FilmMeta getMeta() {
        return meta;
    }

    public void setMeta(FilmMeta meta) {
        this.meta = meta;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getAuthorUsername() {
        return authorUsername;
    }

    public void setAuthorUsername(String authorUsername) {
        this.authorUsername = authorUsername;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getFilm() {
        return film;
    }

    public void setFilm(String film) {
        this.film = film;
    }

    @Override
    public boolean equals(Object anObject) {
        if (!(anObject instanceof Film)) {
            return false;
        }
        Film otherMember = (Film)anObject;
        return otherMember.getId().equals(getId());
    }

    @Override
    public String toString() {
        return "Film{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", author=" + authorID +
                ", description='" + description + '\'' +
                ", url='" + url + '\'' +
                ", meta=" + meta +
                '}';
    }
}
