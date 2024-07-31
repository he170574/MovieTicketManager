package com.movie_theater.controller;

import com.movie_theater.dto.ResponseDTO;
import com.movie_theater.entity.Comment;
import com.movie_theater.entity.Movie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

public class CommentController {


    public ResponseEntity<ResponseDTO> getCommentsOfMovie(@RequestParam Integer movieId) {

        return null;
    }
}
