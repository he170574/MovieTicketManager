package com.movie_theater.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;

@ExtendWith(MockitoExtension.class)
public class ImageServiceTest {
    @InjectMocks
    ImageServiceImpl imageService;

    @Test
    public void testUploadImage() throws IOException {
        File file = new File("D:\\Desktop\\Untitled.png");
        imageService.uploadImage(file);
    }
}
