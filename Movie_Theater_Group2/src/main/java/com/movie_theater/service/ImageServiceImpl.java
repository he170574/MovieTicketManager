package com.movie_theater.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.extern.java.Log;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Log
public class ImageServiceImpl implements ImageService {

    @Override
    public Optional<String> uploadImage(MultipartFile file) {
        return uploadImage(convertMutliPartToTempFile(file));
    }

    @Override
    public Optional<String> uploadImage(File file) {
        Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "de6o7wax8",
                "api_key", "943815565485492",
                "api_secret", "OnsJCIJODMsC8y8UXYV_idEXDPM"));

        try {
            Map uploadResult = cloudinary.uploader().upload(file, ObjectUtils.emptyMap());
            return Optional.of(uploadResult.get("url").toString());
        } catch (IOException e) {
        }
        return Optional.empty();
    }

    private File convertMutliPartToTempFile(MultipartFile file) {
        try {
            File temp = File.createTempFile("temp", null);
            // Chuyển dữ liệu từ MultipartFile sang File
            file.transferTo(temp);
            return temp;
        } catch (IOException e) {
            return null;
        }
    }
}
