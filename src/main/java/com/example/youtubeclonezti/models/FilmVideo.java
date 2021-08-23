package com.example.youtubeclonezti.models;

import org.springframework.data.annotation.Id;

import java.io.InputStream;

public class FilmVideo {

        @Id
        private String id;

        private InputStream stream;

        public FilmVideo(InputStream stream) {
                this.stream = stream;
        }

        public FilmVideo() {
        }

        public String getId() {
                return id;
        }

        public void setId(String id) {
                this.id = id;
        }

        public InputStream getStream() {
                return stream;
        }

        public void setStream(InputStream stream) {
                this.stream = stream;
        }
}
