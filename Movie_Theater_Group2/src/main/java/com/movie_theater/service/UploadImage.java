package com.movie_theater.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.io.File;
import java.util.Map;

public class UploadImage {
    public static String upload(File img) {
        Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "de6o7wax8",
                "api_key", "943815565485492",
                "api_secret", "OnsJCIJODMsC8y8UXYV_idEXDPM"));

        try {
            Map uploadResult = cloudinary.uploader().upload(img, ObjectUtils.emptyMap());
            return (String) uploadResult.get("url");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
