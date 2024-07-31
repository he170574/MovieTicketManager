package com.movie_theater.controller;

import com.movie_theater.dto.*;
import com.movie_theater.entity.*;
import com.movie_theater.entity.key.KeyMovieType;
import com.movie_theater.service.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController
public class MovieController {

    private MovieService movieService;

    private TypeService typeService;
    private TypeMovieService typeMovieService;

    private ScheduleService scheduleService;

    private MovieScheduleService movieScheduleService;

    @Autowired
    public MovieController(MovieService movieService
            , TypeService typeService
            , TypeMovieService typeMovieService
            , ScheduleService scheduleService
            , MovieScheduleService movieScheduleService) {
        this.movieService = movieService;
        this.typeService = typeService;
        this.typeMovieService = typeMovieService;
        this.scheduleService = scheduleService;
        this.movieScheduleService = movieScheduleService;
    }

//        @GetMapping("/admin/get-all-movie")
//    private ResponseEntity<ResponseDTO> getAllMovie() {
//        ResponseDTO responseDTO = new ResponseDTO();
//        List<Movie> movies = movieService.getAll();
//        List<MovieDTO> movieDTOS = new ArrayList<>();
//        for (Movie movie : movies) {
//            movieDTOS.add(movieService.parseMovieToMovieDto(movie));
//        }
//        responseDTO.setData(movieDTOS);
//        responseDTO.setMessage("Get all movie successful");
//        return ResponseEntity.ok().body(responseDTO);
//    }
    @GetMapping("/admin/get-all-movie")
    public ResponseEntity<ResponseDTO> getCinemaRoom (@RequestParam Integer pageNumber,
                                                      @RequestParam Integer pageSize,
                                                      @RequestParam String searchByMovieName) {

        Pageable pageable = PageRequest.of(pageNumber,pageSize);

        Page<Movie> movies = movieService.findByKeywordAndNotDeleted(searchByMovieName, pageable);

        List<Movie> movieList = movies.getContent();

        HashMap<String, Object> mapMovie = new HashMap<>();

        mapMovie.put("listMovies", movieList);
        mapMovie.put("pageNumber",movies.getNumber());
        mapMovie.put("pageSize",movies.getSize());
        mapMovie.put("totalPage",movies.getTotalPages());

        return ResponseEntity.ok().body(ResponseDTO.builder().data(mapMovie).build());
    }
    @GetMapping("/get-movie-by-id")
    private ResponseEntity<ResponseDTO> getMovieById(@RequestParam Integer id) {
        ResponseDTO responseDTO = new ResponseDTO();
        Optional<Movie> movieOptional = movieService.getById(id);
        if (movieOptional.isPresent()) {
            responseDTO.setData(movieService.parseMovieToMovieDto(movieOptional.get()));
            responseDTO.setMessage("Get movie by id successful");
        }
        return ResponseEntity.ok().body(responseDTO);
    }

    @PostMapping("/admin/movie-list/add-new-movie")
    private ResponseEntity<ResponseDTO> saveMovie(@Valid @RequestBody MovieDTO movieDTO) {
        try {
            Movie movie = movieService.parseMovieDtoToMovie(movieDTO);
            movieService.save(movie);

            List<Integer> listTypeId = movieDTO.getTypeMovies();

            List<Type> typeList = typeService.getAll().stream()
                    .filter(type -> listTypeId.contains(type.getTypeId()))
                    .toList();

            for (Type type : typeList) {
                TypeMovie typeMovie = TypeMovie.builder()
                        .type(type)
                        .movie(movie)
                        .keyMovieType(new KeyMovieType(movie.getMovieId(), type.getTypeId()))
                        .deleted(false)
                        .build();
                typeMovieService.save(typeMovie);
                type.getTypeMovies().add(typeMovie);
                movie.getTypeMovies().add(typeMovie);
                typeService.save(type);
            }

            ResponseDTO responseDTO = new ResponseDTO();
            responseDTO.setMessage("Success");
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            e.printStackTrace();
            ResponseDTO responseDTO = new ResponseDTO();
            responseDTO.setMessage("Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
        }
    }


    @PostMapping("/admin/movie-list/edit-movie")
    private ResponseEntity<ResponseDTO> editMovie(@Valid @RequestBody MovieDTO movieDTO) {

        Movie movie = movieService.parseMovieDtoToMovie(movieDTO);
        movie.setMovieId(movieDTO.getMovieId());
        movieService.deleteTypeMovieByMovie(movie);

        List<Integer> listTypeId = movieDTO.getTypeMovies();

        List<Type> typeList = typeService.getAll().stream()
                .filter(type -> listTypeId.contains(type.getTypeId()))
                .toList();

        for (Type type : typeList) {
            TypeMovie typeMovie = TypeMovie.builder()
                    .type(type)
                    .movie(movie)
                    .keyMovieType(new KeyMovieType(movie.getMovieId(), type.getTypeId()))
                    .deleted(false)
                    .build();
            typeMovieService.save(typeMovie);
            type.getTypeMovies().add(typeMovie);
            movie.getTypeMovies().add(typeMovie);
            typeService.save(type);
        }
        movieService.save(movie);
        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setMessage("Success");
        return ResponseEntity.ok().body(responseDTO);
    }


    @GetMapping("/get-all-type")
    private ResponseEntity<ResponseDTO> getAllType() {
        ResponseDTO responseDTO = new ResponseDTO();
        List<TypeDTO> typeDTOS = new ArrayList<>();
        for (Type type : typeService.getAll()) {
            typeDTOS.add(TypeDTO.builder()
                    .typeId(type.getTypeId())
                    .typeName(type.getTypeName())
                    .build());
        }
        responseDTO.setData(typeDTOS);
        responseDTO.setMessage("Get all type successful");
        return ResponseEntity.ok().body(responseDTO);
    }

    @PostMapping("/admin/movie-list/add-new-type")
    private ResponseEntity<ResponseDTO> addNewType(@RequestBody TypeDTO typeDTO) {
        ResponseDTO responseDTO = new ResponseDTO();
        Type type = typeService.save(typeService.parseTypeDtoToType(typeDTO));
        typeDTO.setTypeId(type.getTypeId());
        responseDTO.setData(typeDTO);
        responseDTO.setMessage("Add new type successful");
        return ResponseEntity.ok().body(responseDTO);
    }

    @GetMapping("/get-type-by-id")
    private ResponseEntity<ResponseDTO> getTypeById(@RequestParam Integer id) {
        ResponseDTO responseDTO = new ResponseDTO();
        Optional<Type> type = typeService.getOne(id);
        if (type.isPresent()) {
            responseDTO.setData(TypeDTO.builder()
                    .typeId(type.get().getTypeId())
                    .typeName(type.get().getTypeName())
                    .build());
            responseDTO.setMessage("Get type by id successful");
        }
        return ResponseEntity.ok().body(responseDTO);
    }

    @PostMapping("/admin/movie-list/add-new-schedule")
    private ResponseEntity<ResponseDTO> addNewSchedule(@RequestBody ScheduleDTO scheduleDTO) {
        ResponseDTO responseDTO = new ResponseDTO();
        Schedule schedule = scheduleService.save(scheduleService.parseDtoToEntity(scheduleDTO));
        scheduleDTO.setScheduleId(schedule.getScheduleId());
        responseDTO.setData(scheduleDTO);
        responseDTO.setMessage("Add new type successful");
        return ResponseEntity.ok().body(responseDTO);
    }

    @GetMapping("/get-movie-details")
    private ResponseEntity<ResponseDTO> getMovieDetails(@RequestParam("movieId") Integer movieId) {
        ResponseDTO responseDTO = new ResponseDTO();
        Optional<Movie> movieOptional = movieService.getById(movieId);
        if (movieOptional.isPresent()) {
            responseDTO.setData(movieService.parseMovieToMovieDto(movieOptional.get()));
            responseDTO.setMessage("Get movie by id successful");
        } else {
            responseDTO.setMessage("This movie is not existed");
        }
        return ResponseEntity.ok().body(responseDTO);
    }

    @GetMapping("/get-all-movie-for-member")
    private ResponseEntity<ResponseDTO> getAllMovieForMember(@RequestParam Integer pageNumber,
                                                             @RequestParam Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        Page<Movie> moviePage = movieService.getAllMovieForMember(pageable);

        List<MovieHomeDTO> lstMovieHomeDTO = new ArrayList<>();
        for (Movie movie : moviePage.getContent()) {
            List<String> movieType = movie.getTypeMovies().stream().map(item -> item.getType().getTypeName()).toList();
            lstMovieHomeDTO.add(MovieHomeDTO.builder()
                    .movieId(movie.getMovieId())
                    .movieName(movie.getMovieName())
                    .smallImage(movie.getSmallImage())
                    .typeMovies(movieType)
                    .build());
        }

        HashMap<String, Object> mapMovieHomeDTO = new HashMap<>();
        mapMovieHomeDTO.put("lstMovieHomeDTO", lstMovieHomeDTO);
        mapMovieHomeDTO.put("pageNumber", moviePage.getNumber());
        mapMovieHomeDTO.put("pageSize", moviePage.getSize());
        mapMovieHomeDTO.put("totalPage", moviePage.getTotalPages());

        return ResponseEntity.ok().body(ResponseDTO.builder().message("Success").data(mapMovieHomeDTO).build());
    }

    @GetMapping("/get-movie-schedule-by-movie-id")
    public ResponseEntity<ResponseDTO> getMovieScheduleByMovieId(@RequestParam Integer movieId) {


        Movie movie = movieService.getById(movieId).isEmpty() ? null : movieService.getById(movieId).get();

        if (movie == null) {
            return ResponseEntity.badRequest().body(ResponseDTO.builder().message("Doesn't not exit movie id").build());
        }


        List<MovieSchedule> movieSchedules = movieScheduleService.getByMovie(movie);

        List<Schedule> scheduleList = movieSchedules.stream().map(MovieSchedule::getSchedule).filter(item -> { return item.getScheduleTime().isAfter(LocalDateTime.now()); }).toList();

        List<ScheduleDTO> scheduleDTOList = scheduleList.stream().map(item -> ScheduleDTO.builder()
                .scheduleId(item.getScheduleId())
                .scheduleTime(item.getScheduleTime())
                .deleted(item.getDeleted())
                .build()).toList();
        return ResponseEntity.ok().body(ResponseDTO.builder().message("Success").data(scheduleDTOList).build());
    }
    @GetMapping("/admin/movie-list/delete-movie")
    private ResponseEntity<ResponseDTO> deleteMovie(@RequestParam("movieId") Integer movieId) {
        ResponseDTO responseDTO = new ResponseDTO();
        movieService.updateDeleted(true, movieId);
        responseDTO.setMessage("Delete success");
        return ResponseEntity.ok().body(responseDTO);
    }

    @GetMapping("/get-movie-by-date")
    private ResponseEntity<ResponseDTO> getMovieByDate(@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date){
        List<Movie> lstMovie = movieService.getMoviePlayingByDate(date);

        List<MovieDTO> lstMovieDTO = lstMovie.stream().map(item -> MovieDTO.builder()
                .movieId(item.getMovieId())
                .actor(item.getActor())
                .content(item.getContent())
                .director(item.getDirector())
                .duration(item.getDuration())
                .movieName(item.getMovieName())
                .largeImage(item.getLargeImage())
                .smallImage(item.getSmallImage())
                .deleted(item.getDeleted())
                .typeMoviesString(item.getTypeMovies().stream().map(typeItem -> typeItem.getType().getTypeName()).toList())
                .build()).toList();

        return ResponseEntity.ok().body(ResponseDTO.builder()
                .message("Success")
                .data(lstMovieDTO)
                .build());
    }

    @GetMapping("/get-movie-by-movie-name")
    public ResponseEntity<ResponseDTO> getMovieByMovieName(@RequestParam String movieName,@RequestParam int pageNumber){
        Pageable pageable = PageRequest.of(pageNumber,3);
        Page<Movie> moviePage = movieService.getMovieByMovieName(movieName,pageable);

        List<MovieDTO> lstMovieDTO = moviePage.stream().map(item -> MovieDTO.builder()
                .movieId(item.getMovieId())
                .movieName(item.getMovieName())
                .duration(item.getDuration())
                .smallImage(item.getSmallImage())
                .build()).toList();

        HashMap<String, Object> mapMovie = new HashMap<>();
        mapMovie.put("lstMovie", lstMovieDTO);
        mapMovie.put("pageNumber", moviePage.getNumber());
        mapMovie.put("pageSize", moviePage.getSize());
        mapMovie.put("totalPage", moviePage.getTotalPages());

        return ResponseEntity.ok().body(ResponseDTO.builder()
                        .message("Success")
                        .data(mapMovie)
                .build());
    }

}