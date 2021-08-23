package com.example.youtubeclonezti.controllers;

import com.example.youtubeclonezti.models.*;
import com.example.youtubeclonezti.repositories.CommentRepository;
import com.example.youtubeclonezti.repositories.FilmRepository;
import com.example.youtubeclonezti.security.services.UserDetailsImpl;
import com.mongodb.MongoWriteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {


    @Autowired
    private FilmRepository filmRepository;

    @Autowired
    private CommentRepository commentRepository;

    @GetMapping
    public ResponseEntity<List<Comment>> getComments(@Valid @NotBlank @RequestParam String film_id) {
        Film film = filmRepository.findByIdAndIsActiveTrue(film_id).orElseThrow(() -> new FilmController.FilmNotFoundException("Film not found with id: " + film_id));

        try {

            return new ResponseEntity<>(commentRepository.findAllByFilmIDAndIsActiveTrue(film.getId(), null), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Comment>> getFilmsFilter(@RequestParam(required = false) String film_id,
                                                        @RequestParam(required = false) String titleStarts,
                                                        @RequestParam(required = false) String username,
                                                        @RequestParam(required = false) String startDate,
                                                        @RequestParam(required = false) String endDate) throws ParseException {

            Film film = filmRepository.findByIdAndIsActiveTrue(film_id).orElseThrow(() -> new FilmController.FilmNotFoundException("Film not found with id: " + film_id));

            if (username != null)
                return new ResponseEntity<>(commentRepository.findAllByFilmIDAndAuthorUsernameEqualsAndIsActiveTrue(film.getId(), username), HttpStatus.OK);
            else if (titleStarts != null)
                return new ResponseEntity<>(commentRepository.findAllByFilmIDAndTextContainsAndIsActiveTrue(film.getId(), titleStarts), HttpStatus.OK);
            else if (startDate != null && endDate != null){
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                Date start = formatter.parse(startDate);
                Date end = formatter.parse(endDate);
                return new ResponseEntity<>(commentRepository.findAllByFilmIDAndCreatedDateBetweenAndIsActiveTrue(film.getId(), start, end), HttpStatus.OK);
            }

            return new ResponseEntity<>(commentRepository.findAllByFilmIDAndIsActiveTrue(film.getId(), null), HttpStatus.OK);
    }

    @GetMapping("/sort")
    public ResponseEntity<List<Comment>> getFilmsSort(  @RequestParam(required = false) String film_id,
                                                        @RequestParam(required = false) String username,
                                                        @RequestParam(required = false) String creationDate,
                                                        @RequestParam(required = false) String text) {

            Film film = filmRepository.findByIdAndIsActiveTrue(film_id).orElseThrow(() -> new FilmController.FilmNotFoundException("Film not found with id: " + film_id));

            if (username != null)
                return new ResponseEntity<>(commentRepository.findAllByFilmIDAndIsActiveTrue(film.getId(),
                        Sort.by(username.equals("1") ? Sort.Direction.DESC : Sort.Direction.ASC,
                                "authorUsername")), HttpStatus.OK);
            else if(creationDate != null)
                return new ResponseEntity<>(commentRepository.findAllByFilmIDAndIsActiveTrue(film.getId(),
                        Sort.by(creationDate.equals("1") ? Sort.Direction.DESC : Sort.Direction.ASC,
                                "createdDate")), HttpStatus.OK);
            else if(text != null)
                return new ResponseEntity<>(commentRepository.findAllByFilmIDAndIsActiveTrue(film.getId(),
                        Sort.by(text.equals("1") ? Sort.Direction.DESC : Sort.Direction.ASC,
                                "text")), HttpStatus.OK);

            return new ResponseEntity<>(commentRepository.findAllByFilmIDAndIsActiveTrue(film.getId(), null), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Comment> createComment(@Valid @NotBlank @RequestParam String film_id, @Valid @RequestBody Comment comment) {

        UserDetailsImpl userDetails = (UserDetailsImpl)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        Film _film = filmRepository.findByIdAndIsActiveTrue(film_id).orElseThrow(() -> new FilmController.FilmNotFoundException("Film not found with id: " + film_id));

        try {

            comment.setAuthorUsername(userDetails.getUsername());
            comment.setAuthorID(userDetails.getId());
            comment.setFilmID(_film.getId());

            return new ResponseEntity<>(commentRepository.save(comment), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteComment(@PathVariable String id) {
        UserDetailsImpl userDetails = (UserDetailsImpl)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        Comment _comment = commentRepository.findById(id)
                .orElseThrow(() -> new CommentNotFoundException("Comment not found with id: " + id));

        if (userDetails.getId().equals(_comment.getAuthorID())) {
            try {
                commentRepository.deleteById(id);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Comment> updateComment(@PathVariable String id, @Valid @RequestBody Comment comment) {
        UserDetailsImpl userDetails = (UserDetailsImpl)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        Comment _comment = commentRepository.findById(id)
                .orElseThrow(() -> new CommentNotFoundException("Comment not found with id: " + id));

        if (userDetails.getId().equals(_comment.getAuthorID())) {
            try {
                _comment.setText(comment.getText());
                return new ResponseEntity<>(commentRepository.save(_comment), HttpStatus.OK);
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
                .body(e.toString());
    }

    @ExceptionHandler
    public ResponseEntity<?> handleParseException(ParseException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public class CommentNotFoundException extends RuntimeException
    {
        /**
         *
         */
        private static final long serialVersionUID = 6663501984360907526L;

        public CommentNotFoundException(String exception) {
            super(exception);
        }
    }

}
