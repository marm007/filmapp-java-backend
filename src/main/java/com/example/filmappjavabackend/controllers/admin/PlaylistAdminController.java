package com.example.filmappjavabackend.controllers.admin;

import com.example.filmappjavabackend.models.*;
import com.example.filmappjavabackend.models.updates.AdminActiveUpdate;
import com.example.filmappjavabackend.repositories.PlaylistRepository;
import com.mongodb.MongoWriteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/admin/playlists")
public class PlaylistAdminController {

    @Autowired
    private PlaylistRepository playlistRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Playlist>> getPlaylists() {
        try {

            return new ResponseEntity<>(playlistRepository.findAll(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/filter")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Playlist>> getPlaylistsFilter(@RequestParam(required = false) String title,
                                                             @RequestParam(required = false) String titleStarts,
                                                             @RequestParam(required = false) String startDate,
                                                             @RequestParam(required = false) String endDate) throws ParseException {
        if (title != null)
            return new ResponseEntity<>(playlistRepository.findAllByTitle(title), HttpStatus.OK);
        else if (titleStarts != null)
            return new ResponseEntity<>(playlistRepository.findAllByTitleStartsWith(titleStarts), HttpStatus.OK);
        else if (startDate != null && endDate != null){
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            Date start = formatter.parse(startDate);
            Date end = formatter.parse(endDate);
            return new ResponseEntity<>(playlistRepository.
                    findAllByCreatedDateBetween(start, end), HttpStatus.OK);
        }


        return new ResponseEntity<>(playlistRepository.findAll(), HttpStatus.OK);
    }

    @GetMapping("/sort")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Playlist>> getPlaylistsSort( @RequestParam(required = false) String title,
                                                            @RequestParam(required = false) String creationDate,
                                                            @RequestParam(required = false) String filmsSize) {
        if (title != null)
            return new ResponseEntity<>(playlistRepository.findAll(
                    Sort.by(title.equals("1") ? Sort.Direction.DESC : Sort.Direction.ASC,
                            "title")), HttpStatus.OK);
        else if(creationDate != null)
            return new ResponseEntity<>(playlistRepository.findAll(
                    Sort.by(creationDate.equals("1") ? Sort.Direction.DESC : Sort.Direction.ASC,
                            "createdDate")), HttpStatus.OK);
        else if(filmsSize != null)
            return new ResponseEntity<>(playlistRepository.findAll(
                    Sort.by(filmsSize.equals("1") ? Sort.Direction.DESC : Sort.Direction.ASC,
                            "films")), HttpStatus.OK);
        return new ResponseEntity<>(playlistRepository.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Playlist> getPlaylist(@PathVariable String id) {

        try {
            return new ResponseEntity<>(playlistRepository.findById(id)
                    .orElseThrow(()-> new PlaylistNotFoundException("Playlist not found with id: " + id)), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Playlist> updatePlaylist(@PathVariable String id,
                                                   @Valid @RequestBody AdminActiveUpdate playlistAdminUpdate) {

        Playlist _playlist = playlistRepository.findById(id)
                .orElseThrow(() -> new PlaylistNotFoundException("Playlist not found with id: " + id));

        if (playlistAdminUpdate.getIsActive().equals("active") || playlistAdminUpdate.getIsActive().equals("disable")) {

            boolean _isActive = playlistAdminUpdate.getIsActive().equals("active");
            if (_playlist.isActive() == _isActive) {
                return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
            } else {
                _playlist.setActive(_isActive);
                return new ResponseEntity<>(playlistRepository.save(_playlist), HttpStatus.OK);
            }
        }

        return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
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
        private static final long serialVersionUID = -6513609474569920285L;

        public PlaylistNotFoundException(String exception) {
            super(exception);
        }
    }

}
