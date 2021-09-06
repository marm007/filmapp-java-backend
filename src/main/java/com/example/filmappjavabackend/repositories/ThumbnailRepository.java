package com.example.filmappjavabackend.repositories;

import com.example.filmappjavabackend.models.Thumbnail;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ThumbnailRepository extends MongoRepository<Thumbnail, String> {
}
