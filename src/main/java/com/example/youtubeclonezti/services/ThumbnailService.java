package com.example.youtubeclonezti.services;

import com.example.youtubeclonezti.models.Thumbnail;
import com.example.youtubeclonezti.repositories.ThumbnailRepository;
import com.google.common.io.Files;
import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

// serwis do dodawania zdjęć podglodu filmów, zdjęcia zostają przeskalowane do trzech różnych formatów,
// a następnie zapisane w bazie, typ binary

@Service
public class ThumbnailService {

    @Autowired
    private ThumbnailRepository thumbnailRepository;

    public String addPhoto(MultipartFile file) throws IOException {
        Thumbnail photo = new Thumbnail();
        String extension = Files.getFileExtension(file.getOriginalFilename());

        photo.setOriginal(
                new Binary(BsonBinarySubType.BINARY, file.getBytes()));

        BufferedImage resized = ImageResizer.resize(file.getBytes(), 25);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ImageIO.write(resized, extension, buffer);
        photo.setThumbnail(
                new Binary(BsonBinarySubType.BINARY, buffer.toByteArray()));

        resized = ImageResizer.resize(file.getBytes(), 250);
        buffer = new ByteArrayOutputStream();
        ImageIO.write(resized, extension, buffer);
        photo.setPreview(
                new Binary(BsonBinarySubType.BINARY, buffer.toByteArray()));

        resized = ImageResizer.resize(file.getBytes(), 500);
        buffer = new ByteArrayOutputStream();
        ImageIO.write(resized, extension, buffer);
        photo.setPoster(
                new Binary(BsonBinarySubType.BINARY, buffer.toByteArray()));

        photo = thumbnailRepository.insert(photo);
        return photo.getId();
    }

    public Thumbnail getThumbnail(String id) {
        return thumbnailRepository.findById(id).get();
    }
}