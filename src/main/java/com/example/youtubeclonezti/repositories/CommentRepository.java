package com.example.youtubeclonezti.repositories;

import com.example.youtubeclonezti.models.Comment;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;
public interface CommentRepository extends MongoRepository<Comment, String> {

    List<Comment> findAllByFilmIDAndIsActiveTrue(String filmID, Sort sort);

    List<Comment> findAllByTextContainsAndIsActiveTrue(String text);
    List<Comment> findAllByAuthorUsernameEqualsAndIsActiveTrue(String username);
    List<Comment> findAllByCreatedDateBetweenAndIsActiveTrue(Date start, Date end);

    List<Comment> findAllByFilmIDAndTextContainsAndIsActiveTrue(String filmID, String text);
    List<Comment> findAllByFilmIDAndAuthorUsernameEqualsAndIsActiveTrue(String filmID, String username);
    List<Comment> findAllByFilmIDAndCreatedDateBetweenAndIsActiveTrue(String filmID, Date start, Date end);

    // operacje dostępne dla administratora, biorące pod uwagę wszystkie filmy nawet nieaktywne

    List<Comment> findAllByFilmID(String filmID, Sort sort);

    List<Comment> findAllByTextContains(String text);
    List<Comment> findAllByAuthorUsernameEquals(String username);
    List<Comment> findAllByCreatedDateBetween(Date start, Date end);

    List<Comment> findAllByFilmIDAndTextContains(String filmID, String text);
    List<Comment> findAllByFilmIDAndAuthorUsernameEquals(String filmID, String username);
    List<Comment> findAllByFilmIDAndCreatedDateBetween(String filmID, Date start, Date end);
}
