package com.example.filmappjavabackend.repositories;

import com.example.filmappjavabackend.models.Playlist;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

public interface PlaylistRepository extends MongoRepository<Playlist, String> {

    Playlist findPlaylistById(String id);
    Playlist findPlaylistByTitle(String title);

    // opercaje dla użytkownika nie zalogowanego

    List<Playlist> findAllByIsActiveTrueAndIsPublicTrue(Sort sort);
    List<Playlist> findAllByIsActiveTrueAndIsPublicTrue();

    List<Playlist> findAllByIsActiveTrueAndIsPublicTrueAndTitleStartsWith(String titleStart);
    List<Playlist> findAllByIsActiveTrueAndIsPublicTrueAndTitle(String title);
    List<Playlist> findAllByIsActiveTrueAndIsPublicTrueAndCreatedDateBetween(Date start, Date end);

    // operacje dla użytkownika zalogowanego

    List<Playlist> findAllByIsActiveTrueAndIsPublicTrueOrIsActiveTrueAndAuthorID(String authorID, Sort sort);
    List<Playlist> findAllByIsActiveTrueAndIsPublicTrueOrIsActiveTrueAndAuthorID(String id);

    List<Playlist> findAllByIsActiveTrueAndAuthorIDAndTitleStartsWithOrIsActiveTrueAndIsPublicTrueAndTitleStartsWith (String authorID, String titleStart, String titleStart1);
    List<Playlist> findAllByIsActiveTrueAndIsPublicTrueAndTitleOrIsActiveTrueAndAuthorIDAndTitle(String title, String authorID, String title1);
    List<Playlist> findAllByIsActiveTrueAndIsPublicTrueAndCreatedDateBetweenOrIsActiveTrueAndAuthorIDAndCreatedDateBetween(Date start, Date end, String authorID, Date start1, Date end1);

    // znajdz tylko moje listy

    List<Playlist> findAllByAuthorID(String id);

    // operacje dla administratora

    List<Playlist> findAllByTitle(String title);
    List<Playlist> findAllByTitleStartsWith(String titleStarts);
    List<Playlist> findAllByCreatedDateBetween(Date start, Date end);

}
