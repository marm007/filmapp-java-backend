package com.example.youtubeclonezti.controllers.admin;

import com.example.youtubeclonezti.models.*;
import com.example.youtubeclonezti.repositories.FilmRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/admin/films")
public class FilmAdminController {

    @Autowired
    private FilmRepository filmRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Film>> getFilms() {
        return new ResponseEntity<>(filmRepository.findAll(), HttpStatus.OK);
    }

    @GetMapping("/filter")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Film>> getFilmsFilter(@RequestParam(required = false) String titleStarts,
                                                     @RequestParam(required = false) String username,
                                                     @RequestParam(required = false) String startDate,
                                                     @RequestParam(required = false) String endDate) throws ParseException{
        if (username != null)
            return new ResponseEntity<>(filmRepository.findAllByAuthorUsernameEquals(username), HttpStatus.OK);
        else if (titleStarts != null)
            return new ResponseEntity<>(filmRepository.findAllByTitleStartsWith(titleStarts), HttpStatus.OK);
        else if (startDate != null && endDate != null){
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            Date start = formatter.parse(startDate);
            Date end = formatter.parse(endDate);
            return new ResponseEntity<>(filmRepository.findAllByCreatedDateBetween(start, end), HttpStatus.OK);

        }


        return new ResponseEntity<>(filmRepository.findAll(), HttpStatus.OK);
    }

    @GetMapping("/sort")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Film>> getFilmsSort(  @RequestParam(required = false) String views,
                                                     @RequestParam(required = false) String creationDate,
                                                     @RequestParam(required = false) String likes) {
        if (views != null)
            return new ResponseEntity<>(filmRepository.findAll(
                    Sort.by(views.equals("1") ? Sort.Direction.DESC : Sort.Direction.ASC,
                            "meta.views")), HttpStatus.OK);
        else if(creationDate != null)
            return new ResponseEntity<>(filmRepository.findAll(
                    Sort.by(creationDate.equals("1") ? Sort.Direction.DESC : Sort.Direction.ASC,
                            "createdDate")), HttpStatus.OK);
        else if(likes != null)
            return new ResponseEntity<>(filmRepository.findAll(
                    Sort.by(likes.equals("1") ? Sort.Direction.DESC : Sort.Direction.ASC,
                            "meta.likes")), HttpStatus.OK);

        return new ResponseEntity<>(filmRepository.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Film getFilm(@PathVariable  String id) {
        return filmRepository.findById(id).orElseThrow(() -> new FilmNotFoundException("Actor Not Found"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Film> updateFilm(@PathVariable String id, @Valid @RequestBody AdminActiveUpdate filmAdminUpdate) {

        Film _film = filmRepository.findById(id)
                .orElseThrow(() -> new FilmNotFoundException("Film not found with id: " + id));

        if (filmAdminUpdate.getIsActive().equals("active") || filmAdminUpdate.getIsActive().equals("disable")) {
            boolean _isActive = filmAdminUpdate.getIsActive().equals("active");
            if (_film.isActive() == _isActive) {
                return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
            } else {
                _film.setActive(_isActive);
                return new ResponseEntity<>(filmRepository.save(_film), HttpStatus.OK);
            }
        }

        return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);

    }

    @ExceptionHandler
    public ResponseEntity<?> handleParseException(ParseException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class FilmNotFoundException extends RuntimeException
    {
        /**
         *
         */
        private static final long serialVersionUID = -5873427787796520906L;

        public FilmNotFoundException(String exception) {
            super(exception);
        }
    }
}
