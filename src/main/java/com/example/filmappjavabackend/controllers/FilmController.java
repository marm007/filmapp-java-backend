package com.example.filmappjavabackend.controllers;

import com.example.filmappjavabackend.ApiError;
import com.example.filmappjavabackend.models.*;
import com.example.filmappjavabackend.models.enums.ELike;
import com.example.filmappjavabackend.models.updates.FilmMetaUpdate;
import com.example.filmappjavabackend.models.updates.FilmUpdate;
import com.example.filmappjavabackend.payloads.response.MessageResponse;
import com.example.filmappjavabackend.repositories.FilmRepository;
import com.example.filmappjavabackend.repositories.UserRepository;
import com.example.filmappjavabackend.services.ThumbnailService;
import com.example.filmappjavabackend.security.services.UserDetailsImpl;
import com.example.filmappjavabackend.services.VideoService;
import com.google.common.io.Files;
import com.mongodb.MongoWriteException;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.*;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Film")
@RestController
@Validated
@RequestMapping("/api/films")
public class FilmController {
    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FilmRepository filmRepository;

    @Autowired
    private ThumbnailService thumbnailService;

    @Autowired
    private VideoService videoService;

    @Autowired
    private GridFsOperations operations;


    @GetMapping
    @Async
    public ResponseEntity<List<Film>> getAllFilmsFilterSort( @RequestParam(required = false) String title_starts,
                                                             @RequestParam(required = false) String username,
                                                             @RequestParam(required = false) String start_date,
                                                             @RequestParam(required = false) String end_date,
                                                             @RequestParam(required = false) String filter,
                                                             @RequestParam(required = false) String views,
                                                             @RequestParam(required = false) String creation_date,
                                                             @RequestParam(required = false) String likes) throws ParseException{


        Query query = new Query();
        query.addCriteria(Criteria.where("isActive").is(true));


        if (username != null)
            query.addCriteria(Criteria.where("authorUsername").regex(username));
        if (title_starts != null)
            query.addCriteria(Criteria.where("title").regex("^" + title_starts , "i"));
        if (start_date != null){
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            Date start = formatter.parse(start_date);
            query.addCriteria(Criteria.where("createdDate").gte(start));
        }
        if (end_date != null){
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            Date end = formatter.parse(end_date);
            query.addCriteria(Criteria.where("createdDate").lt(end));
        }
        if (filter != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
            calendar.clear(Calendar.MINUTE);
            calendar.clear(Calendar.SECOND);
            calendar.clear(Calendar.MILLISECOND);
            switch (filter) {
                case "last_hour":
                    calendar = Calendar.getInstance();
                    calendar.add(Calendar.HOUR_OF_DAY, -1);
                    break;
                case "today":
                    break;
                case "this_week":
                    calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
                    break;
                case "this_month":
                    calendar.set(Calendar.DAY_OF_MONTH, 1);
                    break;
                case "this_year":
                    calendar.set(Calendar.DAY_OF_YEAR, 1);
                    break;
            }
            System.out.println(calendar.getTime());
            query.addCriteria(Criteria.where("createdDate").gt(calendar.getTime()));

        }

        if (views != null)
            query.with(Sort.by(views.equals("1") ? Sort.Direction.DESC : Sort.Direction.ASC,
                    "meta.views"));
        else if(creation_date != null)
            query.with(Sort.by(creation_date.equals("1") ? Sort.Direction.DESC : Sort.Direction.ASC,
                    "createdDate"));
        else if(likes != null)
            query.with(Sort.by(likes.equals("1") ? Sort.Direction.DESC : Sort.Direction.ASC,
                    "meta.likes"));

        System.out.println(query);
        List<Film> films = mongoTemplate.find(query, Film.class);
        return new ResponseEntity<>(films, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable  String id) {
        return filmRepository.findByIdAndIsActiveTrue(id).orElseThrow(() -> new FilmNotFoundException("Film Not Found with id: " + id));
    }

    @GetMapping(value = "/{id}/thumbnail/{size}")
    public ResponseEntity<byte[]> getPhoto(@PathVariable String id, @PathVariable String size) {
        Film _film = filmRepository.findByIdAndIsActiveTrue(id).orElseThrow(() -> new FilmNotFoundException("Film Not Found with id: " + id));

        Thumbnail photo = thumbnailService.getThumbnail(_film.getThumbnail());
        byte[] toReturn;
        switch (size) {
            case "original":
                toReturn = photo.getOriginal().getData();
                break;
            case "preview":
                toReturn = photo.getPreview().getData();
                break;
            case "poster":
                toReturn = photo.getPoster().getData();
                break;
            default:
                toReturn = photo.getThumbnail().getData();
                break;
        }

        return ResponseEntity
                .ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(toReturn);
    }

    @GetMapping(value = "/{id}/thumbnail")
    public ResponseEntity<byte[]> getPhotoThumbnail(@PathVariable String id) {
        Film _film = filmRepository.findByIdAndIsActiveTrue(id).orElseThrow(() -> new FilmNotFoundException("Film Not Found with id: " + id));

        Thumbnail photo = thumbnailService.getThumbnail(_film.getThumbnail());
        byte[] toReturn = photo.getThumbnail().getData();

        return ResponseEntity
                .ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(toReturn);
    }

    @GetMapping(value = "/{id}/video")
    public ResponseEntity<InputStreamResource> streamVideo(@PathVariable String id, HttpServletResponse response, HttpServletRequest request) throws Exception {
        Film _film = filmRepository.findByIdAndIsActiveTrue(id).orElseThrow(() -> new FilmNotFoundException("Film Not Found with id: " + id));

        GridFSFile video = videoService.getVideoFile(_film.getFilm());
        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType(video.getMetadata().getString("_contentType")))
                .contentLength(video.getLength())
                .body(operations.getResource(video));
    }

    @PostMapping
    public ResponseEntity<Film> createFilm(@RequestParam @Valid @NotBlank(message = "Title must not be blank")
                                           @Size(min = 10, max = 150, message = "The title must be between {min} and {max} characters long") String title,
                                           @RequestParam @Valid @NotBlank(message = "Description must not be blank")
                                           @Size(min = 10, max = 5000,  message = "The description must be between {min} and {max} characters long") String description,
                                           @RequestParam(required = false) String url,
                                           @RequestParam @Valid @NotNull(message = "Thumbnail must not be null") MultipartFile thumbnail,
                                           @RequestParam(required = false) MultipartFile video) throws IOException {

        if (video == null && url == null) {
            throw new SourceNotFoundException("Video or url must not be null");
        }

        UserDetailsImpl userDetails = (UserDetailsImpl)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        Film film = new Film(title, description);

        String idThumbnail = thumbnailService.addPhoto(thumbnail);
        
        if (video != null) {
            String extension = Files.getFileExtension(video.getOriginalFilename());

            if (!extension.equals("mp4")  &&  !extension.equals("ogv") ) {
                throw new SourceNotFoundException("File must be a video!");
            }
            String idVideo = videoService.addVideo(video);
            film.setFilm(idVideo);
        } else film.setUrl(url);

        film.setThumbnail(idThumbnail);
        film.setAuthorID(userDetails.getId());
        film.setAuthorUsername(userDetails.getUsername());
        try {
            return new ResponseEntity<>(filmRepository.save(film), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Film> updateFilm(@PathVariable String id, @Valid @RequestBody FilmUpdate film) {
        UserDetailsImpl userDetails = (UserDetailsImpl)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        Film _film = filmRepository.findById(id)
                .orElseThrow(() -> new FilmNotFoundException("Film not found with id: " + id));

        if (userDetails.getId().equals(_film.getAuthorID())) {
            if (film.getTitle() != null)
                _film.setTitle(film.getTitle());
            if (film.getDescription() != null)
                _film.setDescription(film.getDescription());
            return new ResponseEntity<>(filmRepository.save(_film), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

    }

    @PutMapping("/{id}/meta/views")
    public ResponseEntity<Film> updateMetaViews(@PathVariable String id, @RequestBody FilmMetaUpdate filmMeta) {

        Film _film = filmRepository.findById(id)
                .orElseThrow(() -> new FilmNotFoundException("Film not found with id: " + id));


        if (filmMeta.isViewed()) {
            _film.getMeta().setViews(_film.getMeta().getViews() + 1);
            try {
                return new ResponseEntity<>(filmRepository.save(_film), HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
            }
        }

        return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
    }

    @PutMapping("/{id}/meta/likes")
    public ResponseEntity<Film> updateMetaLikes(@PathVariable String id, @RequestBody FilmMetaUpdate filmMeta) {


        Film _film = filmRepository.findById(id)
                .orElseThrow(() -> new FilmNotFoundException("Film not found with id: " + id));

        if (filmMeta.getLiked() != ELike.none) {
            System.out.println(filmMeta.getLiked());
            UserDetailsImpl userDetails = (UserDetailsImpl)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());

            User _user = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + userDetails.getUsername()));

            boolean isLiked = _user.getMeta().getLiked().contains(id);
            boolean isDisliked = _user.getMeta().getDisliked().contains(id);

            if (isLiked) {
                if (filmMeta.getLiked() == ELike.liked) {
                    _film.getMeta().setLikes(_film.getMeta().getLikes() - 1);
                    _user.getMeta().getLiked().remove(id);
                } else {
                    _film.getMeta().setLikes(_film.getMeta().getLikes() - 1);
                    _film.getMeta().setDislikes(_film.getMeta().getDislikes() + 1);

                    _user.getMeta().getDisliked().add(id);
                    _user.getMeta().getLiked().remove(id);
                }

            } else if (isDisliked) {
                if (filmMeta.getLiked() == ELike.disliked) {
                    _film.getMeta().setDislikes(_film.getMeta().getDislikes() - 1);
                    _user.getMeta().getDisliked().remove(id);
                } else {
                    _film.getMeta().setDislikes(_film.getMeta().getDislikes() - 1);
                    _film.getMeta().setLikes(_film.getMeta().getLikes() + 1);

                    _user.getMeta().getDisliked().remove(id);
                    _user.getMeta().getLiked().add(id);
                }
            } else {
                if (filmMeta.getLiked() == ELike.liked) {
                    _user.getMeta().getLiked().add(id);
                    _film.getMeta().setLikes(_film.getMeta().getLikes() + 1);
                } else {
                    _user.getMeta().getDisliked().add(id);
                    _film.getMeta().setDislikes(_film.getMeta().getDislikes() + 1);
                }
            }

            userRepository.save(_user);
            return new ResponseEntity<>(filmRepository.save(_film), HttpStatus.OK);

        }

        return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteFilm(@PathVariable String id) {
        UserDetailsImpl userDetails = (UserDetailsImpl)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        Film _film = filmRepository.findById(id)
                .orElseThrow(() -> new FilmNotFoundException("Film not found with id: " + id));

        if (userDetails.getId().equals(_film.getAuthorID())) {
            try {
                filmRepository.deleteById(id);
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
                .body(new MessageResponse("Film with this title already exists!"));
    }

    @ExceptionHandler
    public ResponseEntity<?> handleParseException(ParseException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
    }
    
    @ExceptionHandler({ ConstraintViolationException.class })
    public ResponseEntity<Object> handleConstraintViolation(
            ConstraintViolationException ex, WebRequest request) {
        List<String> errors = new ArrayList<String>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            errors.add(violation.getMessage());
        }

        ApiError apiError =
                    new ApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), errors);
        return new ResponseEntity<Object>(
                apiError, new HttpHeaders(), apiError.getStatus());
    }
   
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class SourceNotFoundException extends RuntimeException
    {
        /**
         *
         */
        private static final long serialVersionUID = -8747554061139873857L;

        public SourceNotFoundException(String exception) {
            super(exception);
        }
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class FilmNotFoundException extends RuntimeException
    {
        /**
         *
         */
        private static final long serialVersionUID = 4571905234816317473L;

        public FilmNotFoundException(String exception) {
            super(exception);
        }
    }
}
