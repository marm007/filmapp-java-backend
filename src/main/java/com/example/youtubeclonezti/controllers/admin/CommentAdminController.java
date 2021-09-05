package com.example.youtubeclonezti.controllers.admin;

import com.example.youtubeclonezti.models.*;
import com.example.youtubeclonezti.models.updates.AdminActiveUpdate;
import com.example.youtubeclonezti.repositories.CommentRepository;
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
@RequestMapping("/api/admin/comments")
public class CommentAdminController {

    @Autowired
    private FilmRepository filmRepository;

    @Autowired
    private CommentRepository commentRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Comment>> getComments(@RequestParam String film_id) {
        if (film_id == null) {
            try {

                return new ResponseEntity<>(commentRepository.findAll(), HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            Film film = filmRepository.findById(film_id).orElseThrow(() -> new FilmAdminController.FilmNotFoundException("Film not found with id: " + film_id));

            try {

                return new ResponseEntity<>(commentRepository.findAllByFilmID(film.getId(), null), HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    @GetMapping("/filter")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Comment>> getFilmsFilter(@RequestParam(required = false) String titleStarts,
                                                        @RequestParam(required = false) String username,
                                                        @RequestParam(required = false) String startDate,
                                                        @RequestParam(required = false) String endDate) throws ParseException {

            if (username != null)
                return new ResponseEntity<>(commentRepository.findAllByAuthorUsernameEquals(username), HttpStatus.OK);
            else if (titleStarts != null)
                return new ResponseEntity<>(commentRepository.findAllByTextContains(titleStarts), HttpStatus.OK);
            else if (startDate != null && endDate != null){
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                Date start = formatter.parse(startDate);
                Date end = formatter.parse(endDate);
                return new ResponseEntity<>(commentRepository.findAllByCreatedDateBetween(start, end), HttpStatus.OK);
            }

            return new ResponseEntity<>(commentRepository.findAll(), HttpStatus.OK);
    }

    @GetMapping("/sort")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Comment>> getFilmsSort(  @RequestParam(required = false) String username,
                                                        @RequestParam(required = false) String creationDate,
                                                        @RequestParam(required = false) String text) {

            if (username != null)
                return new ResponseEntity<>(commentRepository.findAll(
                        Sort.by(username.equals("1") ? Sort.Direction.DESC : Sort.Direction.ASC,
                                "authorUsername")), HttpStatus.OK);
            else if(creationDate != null)
                return new ResponseEntity<>(commentRepository.findAll(
                        Sort.by(creationDate.equals("1") ? Sort.Direction.DESC : Sort.Direction.ASC,
                                "createdDate")), HttpStatus.OK);
            else if(text != null)
                return new ResponseEntity<>(commentRepository.findAll(
                        Sort.by(text.equals("1") ? Sort.Direction.DESC : Sort.Direction.ASC,
                                "text")), HttpStatus.OK);

            return new ResponseEntity<>(commentRepository.findAll(), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Comment> updateComment(@PathVariable String id, @Valid @RequestBody AdminActiveUpdate commentAdminUpdate) {

        Comment _comment = commentRepository.findById(id)
                .orElseThrow(() -> new CommentNotFoundException("Comment not found with id: " + id));

        if (commentAdminUpdate.getIsActive().equals("active") || commentAdminUpdate.getIsActive().equals("disable")) {

            boolean _isActive = commentAdminUpdate.getIsActive().equals("active");
            if (_comment.isActive() == _isActive) {
                return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
            } else {
                _comment.setActive(_isActive);
                return new ResponseEntity<>(commentRepository.save(_comment), HttpStatus.OK);
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
    public class CommentNotFoundException extends RuntimeException
    {
        /**
         *
         */
        private static final long serialVersionUID = 1022664967078623454L;

        public CommentNotFoundException(String exception) {
            super(exception);
        }
    }

}
