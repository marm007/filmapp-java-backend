package com.example.youtubeclonezti.models;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;


// tworzenie indeksu powiązanego z polem title oraz authorID playlisty,
// uzytkownik nie może stworzyć playlisty jesli posiada stworzoną playliste o tym tytutle
// tytuly mogą być takie same dla różnych użytkowników

@Document(collection = "playlists")
@CompoundIndexes({
        @CompoundIndex(name = "title_authorID", def = "{'title' : 1, 'authorID': 1}", unique = true)
})
public class Playlist {

    @Id
    private ObjectId id;

    @NotBlank
    private String title;

    private String authorID;

    private String authorUsername;

    private List<String> films;

    private boolean isPublic = true;

    private boolean isActive = true;

    @CreatedDate
    private Date createdDate;

    public Playlist() {
    }


    public Playlist(String title) {
        this.title = title;
    }

    public Playlist(String title, String authorID) {
        this.title = title;
        this.authorID = authorID;
    }

    public Playlist(String title, boolean isPublic) {
        this.title = title;
        this.isPublic = isPublic;
    }

    public Playlist(String title, String authorID, boolean isPublic) {
        this.title = title;
        this.isPublic = isPublic;
        this.authorID = authorID;
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

    public List<String> getFilms() {
        return films;
    }

    public void setFilms(List<String> films) {
        this.films = films;
    }

    public void updateFilms(List<String> films) {
        this.films.addAll(films);
    }

    public void removeFilms(List<String> films) {
        this.films.removeAll(films);
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public String getAuthorUsername() {
        return authorUsername;
    }

    public void setAuthorUsername(String authorUsername) {
        this.authorUsername = authorUsername;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public boolean equals(Object anObject) {
        if (!(anObject instanceof Playlist)) {
            return false;
        }
        Playlist otherMember = (Playlist)anObject;
        return otherMember.getId().equals(getId()) || (otherMember.getTitle().equals(getTitle()) && otherMember.getAuthorID().equals(getAuthorID()));
    }

    @Override
    public String toString() {
        return title;
    }
}
