package com.movie_theater.controller;

import com.movie_theater.dto.FoodDTO;
import com.movie_theater.dto.ResponseDTO;
import com.movie_theater.entity.Food;
import com.movie_theater.final_attribute.ROLE;
import com.movie_theater.security.CustomAccount;
import com.movie_theater.service.FoodService;
import com.movie_theater.service.UploadImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;


@RestController
public class FoodController {
    @Autowired
    FoodService foodService;

    @PostMapping("/add-food")
    private ResponseEntity<ResponseDTO> addNewFood(@ModelAttribute FoodDTO foodDTO, Authentication authentication) throws IOException {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseDTO.builder().message("Login before").build());
        }

        if (!((CustomAccount) authentication.getPrincipal()).getRole().equals(ROLE.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseDTO.builder().message("Don't have permission").build());
        }
        //Upload Image
        // Tạo một file tạm thời
        File file = File.createTempFile("temp", null);
        // Chuyển dữ liệu từ MultipartFile sang File
        foodDTO.getFoodImageFile().transferTo(file);
        if (!foodDTO.getFoodImageFile().isEmpty()) {
            String imageUrl = UploadImage.upload(file);

            if (imageUrl == null) {
                return ResponseEntity.badRequest().body(ResponseDTO.builder().message("Can't update image now").build());
            }

            foodDTO.setFoodImage(imageUrl);
        }

        Food food = Food.builder()
                .foodName(foodDTO.getFoodName())
                .foodImage(foodDTO.getFoodImage())
                .foodSize(foodDTO.getFoodSize())
                .foodPrice(foodDTO.getFoodPrice())
                .deleted(false)
                .build();

        Food foodSaved = foodService.save(food);
        if (foodSaved != null) {
            return ResponseEntity.ok().body(ResponseDTO.builder().message("Add New Success").build());
        }
        return ResponseEntity.badRequest().body(ResponseDTO.builder().message("Internal Error. PLease try later").build());
    }

    @GetMapping("/get-all-food")
    public ResponseEntity<ResponseDTO> getAllFood(@RequestParam Integer pageNumber,
                                                  @RequestParam Integer pageSize,
                                                  @RequestParam String foodName) {

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Food> pageFood = foodService.getAllByFoodNameContainsAndDeletedIsFalse(foodName, pageable);

        List<FoodDTO> lstFoodDTO = pageFood.stream().map(item -> FoodDTO.builder()
                .foodName(item.getFoodName())
                .foodSize(item.getFoodSize())
                .foodPrice(item.getFoodPrice())
                .foodImage(item.getFoodImage())
                .foodId(item.getFoodId())
                .build()).toList();

        HashMap<String, Object> mapEmployee = new HashMap<>();
        mapEmployee.put("lstFood", lstFoodDTO);
        mapEmployee.put("pageNumber", pageFood.getNumber());
        mapEmployee.put("pageSize", pageFood.getSize());
        mapEmployee.put("totalPage", pageFood.getTotalPages());

        return ResponseEntity.ok().body(ResponseDTO.builder().
                message("Success").
                data(mapEmployee).
                build());
    }

    @GetMapping("/get-food-by-id")
    public ResponseEntity<ResponseDTO> getFoodByFoodId(@RequestParam Integer foodId) {
        Food food = foodService.getById(foodId);
        if (food == null) {
            return ResponseEntity.badRequest().body(ResponseDTO.builder().
                    message("Doesn't exit food id")
                    .build());
        }

        return ResponseEntity.ok().body(ResponseDTO.builder().
                message("Success").
                data(FoodDTO.builder()
                        .foodId(food.getFoodId())
                        .foodImage(food.getFoodImage())
                        .foodName(food.getFoodName())
                        .foodPrice(food.getFoodPrice())
                        .foodSize(food.getFoodSize())
                        .build())
                .build());
    }

    @PostMapping("/update-food")
    public ResponseEntity<ResponseDTO> updateFood(@ModelAttribute FoodDTO foodDTO, Authentication authentication) throws IOException {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseDTO.builder().message("Login before").build());
        }

        if (!((CustomAccount) authentication.getPrincipal()).getRole().equals(ROLE.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseDTO.builder().message("Don't have permission").build());
        }



        //Upload Image
        // Tạo một file tạm thời
        if (foodDTO.getFoodImageFile() != null && !foodDTO.getFoodImageFile().isEmpty()) {
            File file = File.createTempFile("temp", null);
            // Chuyển dữ liệu từ MultipartFile sang File
            foodDTO.getFoodImageFile().transferTo(file);
            String imageUrl = UploadImage.upload(file);

            if (imageUrl == null) {
                return ResponseEntity.badRequest().body(ResponseDTO.builder().message("Can't update image now").build());
            }

            foodDTO.setFoodImage(imageUrl);
        }

        Food food = foodService.getById(foodDTO.getFoodId());
        if(foodDTO.getFoodImage() != null && !foodDTO.getFoodImage().isEmpty()){
            food.setFoodImage(foodDTO.getFoodImage());
        }
        food.setFoodName(foodDTO.getFoodName());
        food.setFoodSize(foodDTO.getFoodSize());
        food.setFoodPrice(foodDTO.getFoodPrice());

        Food foodSaved = foodService.save(food);
        if (foodSaved != null) {
            return ResponseEntity.ok().body(ResponseDTO.builder().message("Update Success").build());
        }

        return ResponseEntity.badRequest().body(ResponseDTO.builder()
                .message("Update Fail")
                .build());
    }

    @GetMapping("delete-food-by-id")
    public ResponseEntity<ResponseDTO> deleteFoodById(@RequestParam Integer foodId, Authentication authentication){
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseDTO.builder().message("Login before").build());
        }

        if (!((CustomAccount) authentication.getPrincipal()).getRole().equals(ROLE.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseDTO.builder().message("Don't have permission").build());
        }

        Food food = foodService.getById(foodId);

        if(food == null){
            return ResponseEntity
                    .badRequest()
                    .body(ResponseDTO.builder()
                            .message("Doesn't exit food id")
                            .build());
        }

        foodService.deActiveFoodByFoodId(foodId);

        return ResponseEntity.ok().body(ResponseDTO.builder()
                .message("Delete Success")
                .build());
    }
}