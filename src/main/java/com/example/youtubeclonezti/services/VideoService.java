package com.example.youtubeclonezti.services;

import com.example.youtubeclonezti.models.FilmVideo;
import com.google.common.io.Files;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

// serwis do dodawania filmów do bazy danych przy pomocy gridfs

@Service
public class VideoService {

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFsOperations operations;

    public String addVideo(MultipartFile file) throws IOException {

        String filename = Files.getNameWithoutExtension(file.getOriginalFilename())
                + System.currentTimeMillis() + "."
                + Files.getFileExtension(file.getOriginalFilename());
        DBObject metaData = new BasicDBObject();
        metaData.put("type", "video");
        metaData.put("originalName", file.getOriginalFilename());
        ObjectId id = gridFsTemplate.store(
                file.getInputStream(), filename, file.getContentType(), metaData);
        return id.toString();
    }

    public FilmVideo getVideo(String id) throws IllegalStateException, IOException {
        GridFSFile file = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(id)));
        FilmVideo video = new FilmVideo();
        video.setStream(operations.getResource(file).getInputStream());
        return video;
    }

    public GridFSFile getVideoFile(String id) throws IllegalStateException, IOException {
        GridFSFile file = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(id)));
        return file;
    }
}

