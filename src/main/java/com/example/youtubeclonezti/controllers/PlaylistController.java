package com.example.youtubeclonezti.controllers;

import com.example.youtubeclonezti.models.*;
import com.example.youtubeclonezti.payloads.response.MessageResponse;
import com.example.youtubeclonezti.repositories.FilmRepository;
import com.example.youtubeclonezti.repositories.PlaylistRepository;
import com.example.youtubeclonezti.security.jwt.JWTUtils;
import com.example.youtubeclonezti.security.services.UserDetailsImpl;
import com.google.common.collect.Lists;
import com.mongodb.MongoWriteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.dao.DuplicateKeyException;

import javax.validation.Valid;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/playlists")
public class PlaylistController {

    @Autowired
    JWTUtils jwtUtils;

    @Autowired
    private PlaylistRepository playlistRepository;

    @Autowired
    private FilmRepository filmRepository;

    private boolean isAuthenticated() {
        return !SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals("anonymousUser"); // zwraca true jeśli użytkownik jest zalogowany
    }

    @GetMapping
    public ResponseEntity<List<Playlist>> getPlaylists() {
        if (!isAuthenticated()) {
            try {
                return new ResponseEntity<>(playlistRepository.findAllByIsActiveTrueAndIsPublicTrue(), HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            UserDetailsImpl userDetails = (UserDetailsImpl)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());

            try {

                return new ResponseEntity<>(playlistRepository.findAllByIsActiveTrueAndIsPublicTrueOrIsActiveTrueAndAuthorID(userDetails.getId()), HttpStatus.OK);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

    }

    @GetMapping("/filter")
    public ResponseEntity<List<Playlist>> getPlaylistsFilter(@RequestParam(required = false) String title,
                                                             @RequestParam(required = false) String titleStarts,
                                                             @RequestParam(required = false) String startDate,
                                                             @RequestParam(required = false) String endDate) throws ParseException {
        if (isAuthenticated()) {

            UserDetailsImpl userDetails = (UserDetailsImpl)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());

            if (title != null)
                return new ResponseEntity<>(playlistRepository.
                        findAllByIsActiveTrueAndIsPublicTrueAndTitleOrIsActiveTrueAndAuthorIDAndTitle(title, userDetails.getId(), title), HttpStatus.OK);
            else if (titleStarts != null)
                return new ResponseEntity<>(playlistRepository
                        .findAllByIsActiveTrueAndAuthorIDAndTitleStartsWithOrIsActiveTrueAndIsPublicTrueAndTitleStartsWith
                                (userDetails.getId(), titleStarts, titleStarts), HttpStatus.OK);
            else if (startDate != null && endDate != null){
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                Date start = formatter.parse(startDate);
                Date end = formatter.parse(endDate);
                return new ResponseEntity<>(playlistRepository.
                        findAllByIsActiveTrueAndIsPublicTrueAndCreatedDateBetweenOrIsActiveTrueAndAuthorIDAndCreatedDateBetween
                                (start, end, userDetails.getId(), start, end), HttpStatus.OK);
            }

            return new ResponseEntity<>(playlistRepository.findAllByIsActiveTrueAndIsPublicTrueOrIsActiveTrueAndAuthorID(userDetails.getId()), HttpStatus.OK);
        } else {
            if (title != null)
                return new ResponseEntity<>(playlistRepository.findAllByIsActiveTrueAndIsPublicTrueAndTitle(title), HttpStatus.OK);
            else if (titleStarts != null)
                return new ResponseEntity<>(playlistRepository.findAllByIsActiveTrueAndIsPublicTrueAndTitleStartsWith(titleStarts), HttpStatus.OK);
            else if (startDate != null && endDate != null){
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                Date start = formatter.parse(startDate);
                Date end = formatter.parse(endDate);
                return new ResponseEntity<>(playlistRepository.
                        findAllByIsActiveTrueAndIsPublicTrueAndCreatedDateBetween(start, end), HttpStatus.OK);
            }


            return new ResponseEntity<>(playlistRepository.findAllByIsActiveTrueAndIsPublicTrue(), HttpStatus.OK);
        }


    }

    @GetMapping("/sort")
    public ResponseEntity<List<Playlist>> getPlaylistsSort( @RequestParam(required = false) String title,
                                                            @RequestParam(required = false) String creationDate,
                                                            @RequestParam(required = false) String filmsSize) {
        if (isAuthenticated()) {
            UserDetailsImpl userDetails = (UserDetailsImpl) (SecurityContextHolder.getContext().getAuthentication().getPrincipal());

            if (title != null)
                return new ResponseEntity<>(playlistRepository.findAllByIsActiveTrueAndIsPublicTrueOrIsActiveTrueAndAuthorID(
                        userDetails.getId(),
                        Sort.by(title.equals("1") ? Sort.Direction.DESC : Sort.Direction.ASC,
                                "title")), HttpStatus.OK);
            else if (creationDate != null)
                return new ResponseEntity<>(playlistRepository.findAllByIsActiveTrueAndIsPublicTrueOrIsActiveTrueAndAuthorID(
                        userDetails.getId(),
                        Sort.by(creationDate.equals("1") ? Sort.Direction.DESC : Sort.Direction.ASC,
                                "createdDate")), HttpStatus.OK);
            else if (filmsSize != null)
                return new ResponseEntity<>(playlistRepository.findAllByIsActiveTrueAndIsPublicTrueOrIsActiveTrueAndAuthorID(
                        userDetails.getId(),
                        Sort.by(filmsSize.equals("1") ? Sort.Direction.DESC : Sort.Direction.ASC,
                                "films")), HttpStatus.OK);
            return new ResponseEntity<>(playlistRepository
                    .findAllByIsActiveTrueAndIsPublicTrueOrIsActiveTrueAndAuthorID(userDetails.getId()), HttpStatus.OK);

        } else {


            if (title != null)
                return new ResponseEntity<>(playlistRepository.findAllByIsActiveTrueAndIsPublicTrue(
                        Sort.by(title.equals("1") ? Sort.Direction.DESC : Sort.Direction.ASC,
                                "title")), HttpStatus.OK);
            else if (creationDate != null)
                return new ResponseEntity<>(playlistRepository.findAllByIsActiveTrueAndIsPublicTrue(
                        Sort.by(creationDate.equals("1") ? Sort.Direction.DESC : Sort.Direction.ASC,
                                "createdDate")), HttpStatus.OK);
            else if (filmsSize != null)
                return new ResponseEntity<>(playlistRepository.findAllByIsActiveTrueAndIsPublicTrue(
                        Sort.by(filmsSize.equals("1") ? Sort.Direction.DESC : Sort.Direction.ASC,
                                "films")), HttpStatus.OK);
            return new ResponseEntity<>(playlistRepository.findAllByIsActiveTrueAndIsPublicTrue(), HttpStatus.OK);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Playlist> getPlaylists(@PathVariable String id) {
        Playlist playlist = playlistRepository.findById(id)
                .orElseThrow(()-> new PlaylistNotFoundException("Playlist not found with id: " + id));

        if (playlist.isPublic())
            return new ResponseEntity<>(playlist, HttpStatus.OK);
        else {
            if (isAuthenticated()) {
                UserDetailsImpl userDetails = (UserDetailsImpl) (SecurityContextHolder.getContext().getAuthentication().getPrincipal());

                if (playlist.getAuthorID().equals(userDetails.getId())) {
                    return new ResponseEntity<>(playlist, HttpStatus.OK);
                } else
                    return new ResponseEntity<>(HttpStatus.FORBIDDEN);

            } else {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }
    }

    @GetMapping("/{id}/films")
    public ResponseEntity<List<Film>> getPlaylistFilms(@PathVariable String id) {
        Playlist playlist = playlistRepository.findById(id)
                .orElseThrow(()-> new PlaylistNotFoundException("Playlist not found with id: " + id));

        if (playlist.isPublic())
            return new ResponseEntity<>(Lists.newArrayList(filmRepository.findAllById(playlist.getFilms())), HttpStatus.OK);
        else {
            if (isAuthenticated()) {
                UserDetailsImpl userDetails = (UserDetailsImpl) (SecurityContextHolder.getContext().getAuthentication().getPrincipal());

                if (playlist.getAuthorID().equals(userDetails.getId())) {
                    return new ResponseEntity<>(Lists.newArrayList(filmRepository.findAllById(playlist.getFilms())), HttpStatus.OK);
                } else
                    return new ResponseEntity<>(HttpStatus.FORBIDDEN);

            } else {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }
    }

    @PostMapping
    public ResponseEntity<Playlist> createPlaylist(@Valid @RequestBody Playlist playlist) throws DuplicateKeyException{
        UserDetailsImpl userDetails = (UserDetailsImpl)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        playlist.setAuthorID(userDetails.getId());
        playlist.setAuthorUsername(userDetails.getUsername());
        return new ResponseEntity<>(playlistRepository.save(playlist), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Playlist> updatePlaylist(@PathVariable String id, @RequestBody PlaylistUpdate playlistUpdate) {
        UserDetailsImpl userDetails = (UserDetailsImpl) (SecurityContextHolder.getContext().getAuthentication().getPrincipal());


        Playlist _playlist = playlistRepository.findById(id)
                .orElseThrow(() -> new PlaylistNotFoundException("Playlist not found with id: " + id));

        if (userDetails.getId().equals(_playlist.getAuthorID())) {
            List<String> _films = new ArrayList<>();

            if (playlistUpdate.getFilms() != null) {

                List<String> _filmsDistinct = playlistUpdate.getFilms().stream().distinct().collect(Collectors.toList());

                filmRepository.findAllById(_filmsDistinct).forEach(film -> {
                    if (_playlist.getFilms() == null || !_playlist.getFilms().contains(film.getId()) || playlistUpdate.isRemoveFilms())
                        _films.add(film.getId());
                });
            }
            try {
                if (_playlist.getFilms() == null){
                    if (!playlistUpdate.isRemoveFilms())
                        _playlist.setFilms(_films);
                }
                else {
                    if (!playlistUpdate.isRemoveFilms())
                        _playlist.updateFilms(_films);
                    else
                        _playlist.removeFilms(_films);
                }

                if (playlistUpdate.getTitle() != null)
                    _playlist.setTitle(playlistUpdate.getTitle());

                    _playlist.setPublic(playlistUpdate.getIsPublic());

                return new ResponseEntity<>(playlistRepository.save(_playlist), HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deletePlaylist(@PathVariable String id) {
        UserDetailsImpl userDetails = (UserDetailsImpl)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());


        Playlist _playlist = playlistRepository.findById(id)
                .orElseThrow(() -> new PlaylistNotFoundException("Playlist not found with id: " + id));

        if (userDetails.getId().equals(_playlist.getAuthorID())) {
            try {
                playlistRepository.delete(_playlist);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @ExceptionHandler
    public ResponseEntity<?> mongoWriteException(MongoWriteException e) throws IOException {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new MessageResponse("You have alread created playlist with this title!"));
    }

    @ExceptionHandler(ParseException.class)
    public ResponseEntity<?> handleParseException(ParseException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public class PlaylistNotFoundException extends RuntimeException
    {
        /**
         *
         */
        private static final long serialVersionUID = -9071943780576536601L;

        public PlaylistNotFoundException(String exception) {
            super(exception);
        }
    }

}
