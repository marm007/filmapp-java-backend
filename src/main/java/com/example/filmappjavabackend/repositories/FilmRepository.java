package com.example.filmappjavabackend.repositories;

import com.example.filmappjavabackend.models.Film;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface FilmRepository extends MongoRepository<Film, String> {


    Optional<Film> findByIdAndIsActiveTrue(String id);

    // wyszukiwanie dla admina bez opcji isActive

    List<Film> findAllByTitleStartsWith(String titleStart);
    List<Film> findAllByAuthorUsernameEquals(String username);
    List<Film> findAllByCreatedDateBetween(Date start, Date end);

    // wyszukiwanie filmów danego użytkownika, ale wykorzystywane tylko przez zalogowanego użytkownika do
    // wyszukania jego filmów

    List<Film> findAllByAuthorID(String id);
}
