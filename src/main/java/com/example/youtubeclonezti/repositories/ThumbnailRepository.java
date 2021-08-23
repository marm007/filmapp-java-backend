package com.example.youtubeclonezti.repositories;

import com.example.youtubeclonezti.models.Thumbnail;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ThumbnailRepository extends MongoRepository<Thumbnail, String> {
}
