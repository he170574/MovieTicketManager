package com.movie_theater.controller;

import com.movie_theater.dto.ResponseDTO;
import com.movie_theater.entity.Movie;
import com.movie_theater.security.CustomAccount;
import com.movie_theater.service.AccountService;
import com.movie_theater.service.MovieService;
import com.movie_theater.service.PromotionService;
import com.movie_theater.service.UploadImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Optional;

@RestController
public class FileController {

    AccountService accountService;
    MovieService movieService;
    PromotionService promotionService;

    @Autowired
    public FileController(AccountService accountService, MovieService movieService, PromotionService promotionService) {
        this.accountService = accountService;
        this.movieService = movieService;
        this.promotionService = promotionService;
    }

    @PostMapping("/save-avatar-image")
    public ResponseEntity<ResponseDTO> uploadFile(@RequestParam("file") MultipartFile multipartFile, Authentication authentication){
        try{
            //Kiểm tra login
            if (authentication == null){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseDTO.builder().message("No Permission").build());
            }
            CustomAccount customAccount = (CustomAccount) authentication.getPrincipal();
            // Tạo một file tạm thời
            File file = File.createTempFile("temp", null);
            // Chuyển dữ liệu từ MultipartFile sang File
            multipartFile.transferTo(file);
            String fileUrl = UploadImage.upload(file);
            if (fileUrl == null){
                return ResponseEntity.badRequest().body(ResponseDTO.builder().message("Can't update now").build());
            }
            accountService.updateAccountImage(customAccount.getUsername(),fileUrl);

            return ResponseEntity.ok().body(ResponseDTO.builder().message("success").data(fileUrl).build());
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ResponseDTO.builder().message(e.getMessage()).build());
        }
    }

    @PostMapping("/save-small-movie-image")
    public ResponseEntity<ResponseDTO> uploadSmallImage(@RequestParam("file") MultipartFile multipartFile
            ,@RequestParam("movie-id") String movieId, Authentication authentication){
        try{
            //Kiểm tra login
            if (authentication == null){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseDTO.builder().message("No Permission").build());
            }
            CustomAccount customAccount = (CustomAccount) authentication.getPrincipal();

            if (!customAccount.getRole().equals("ADMIN")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseDTO.builder().message("No Permission").build());
            }
            // Tạo một file tạm thời
            File file = File.createTempFile("temp", null);
            // Chuyển dữ liệu từ MultipartFile sang File
            multipartFile.transferTo(file);

            String fileUrl = UploadImage.upload(file);

            if (fileUrl == null){
                return ResponseEntity.badRequest().body(ResponseDTO.builder().message("Can't update now").build());
            }

            Optional<Movie> movieOptional = movieService.getById(Integer.parseInt(movieId));
            if (movieOptional.isPresent()) {
                Movie movie = movieOptional.get();
                movie.setSmallImage(fileUrl);
                movieService.save(movie);
                return ResponseEntity.ok().body(ResponseDTO.builder().message("success").data(fileUrl).build());
            }
            return ResponseEntity.badRequest().body(ResponseDTO.builder().message("Can not find movie").build());
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ResponseDTO.builder().message(e.getMessage()).build());
        }
    }

    @PostMapping("/save-large-movie-image")
    public ResponseEntity<ResponseDTO> uploadLargeImage(@RequestParam("file") MultipartFile multipartFile
            , @RequestParam("movie-id") String movieId, Authentication authentication){
        try{
            //Kiểm tra login
            if (authentication == null){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseDTO.builder().message("No Permission").build());
            }
            CustomAccount customAccount = (CustomAccount) authentication.getPrincipal();

            if (!customAccount.getRole().equals("ADMIN")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseDTO.builder().message("No Permission").build());
            }
            // Tạo một file tạm thời
            File file = File.createTempFile("temp", null);
            // Chuyển dữ liệu từ MultipartFile sang File
            multipartFile.transferTo(file);

            String fileUrl = UploadImage.upload(file);

            if (fileUrl == null){
                return ResponseEntity.badRequest().body(ResponseDTO.builder().message("Can't update now").build());
            }

            Optional<Movie> movieOptional = movieService.getById(Integer.parseInt(movieId));
            if (movieOptional.isPresent()) {
                Movie movie = movieOptional.get();
                movie.setLargeImage(fileUrl);
                movieService.save(movie);
                return ResponseEntity.ok().body(ResponseDTO.builder().message("success").data(fileUrl).build());
            }
            return ResponseEntity.badRequest().body(ResponseDTO.builder().message("Can not find movie").build());
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ResponseDTO.builder().message(e.getMessage()).build());
        }
    }

    @PostMapping("/upload-image-source")
    public ResponseEntity<ResponseDTO> uploadImageSource(@RequestParam("file") MultipartFile multipartFile){
        try{
            File file = File.createTempFile("temp", null);
            // Chuyển dữ liệu từ MultipartFile sang File
            multipartFile.transferTo(file);
            String fileUrl = UploadImage.upload(file);
            if (fileUrl == null){
                return ResponseEntity.badRequest().body(ResponseDTO.builder().message("Image is empty").build());
            }
            return ResponseEntity.ok().body(ResponseDTO.builder().message("Upload image successful").data(fileUrl).build());
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ResponseDTO.builder().message(e.getMessage()).build());
        }
    }
}
