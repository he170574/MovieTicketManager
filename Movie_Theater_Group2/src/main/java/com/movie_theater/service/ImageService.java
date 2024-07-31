package com.movie_theater.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public interface ImageService {
    Optional<String> uploadImage(MultipartFile file);

    Optional<String> uploadImage(File file) throws IOException;
}
