package com.movie_theater.controller;

import com.movie_theater.dto.PromotionDTO;
import com.movie_theater.dto.ResponseDTO;
import com.movie_theater.entity.Promotion;
import com.movie_theater.service.PromotionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
public class PromotionController {
    private PromotionService promotionService;

    @Autowired
    public PromotionController(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    @GetMapping("/get-promotion")
    public ResponseEntity<ResponseDTO> getPromotion() {
        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setMessage("Get success");
        responseDTO.setData(promotionService.getAll());
        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }

    @PostMapping ("/edit-promotion")
    public ResponseEntity<ResponseDTO> getPromotionById(@Valid @RequestBody PromotionDTO promotionDTO, BindingResult bindingResult) {

        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setMessage("Get success");
        responseDTO.setData(promotionService.getOne(promotionDTO.getPromotionId()));
        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }

    @PostMapping ("/save-new-promotion")
    public ResponseEntity<ResponseDTO> getAPromotion(@RequestBody PromotionDTO promotionDTO) {
        ResponseDTO responseDTO = new ResponseDTO();
        Promotion promotion = promotionService.parsePromotionDtoToPromotion(promotionDTO);
        responseDTO.setMessage("Get success");
        responseDTO.setData(promotionService.save(promotion));
        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }

    @PostMapping ("/delete-promotion")
    public ResponseEntity<ResponseDTO> deletePromotion(@RequestParam Integer id) {
        ResponseDTO responseDTO = new ResponseDTO();
        try {
            promotionService.deleteByPromotionId(id);
            responseDTO.setMessage("Success");
            responseDTO.setData("Delete success"); // No data needed for delete response
            return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
        } catch (Exception e) {
            responseDTO.setMessage("Delete failed: " + e.getMessage());
            responseDTO.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDTO);
        }
    }

    @PostMapping("/submitForm")
    public String submitForm(@Valid @RequestBody PromotionDTO promotionDTO, BindingResult bindingResult) {
        if (promotionDTO.getStartTime().isBefore(promotionDTO.getEndTime())) {
            bindingResult.addError(new FieldError("promotionDTO", "fieldName", "Your custom error message"));
        }
        if (bindingResult.hasErrors()) {

            return "Validation failed";
        }

        return "Form submitted successfully";
    }

    @PostMapping ("/save-edit-promotion")
    public ResponseEntity<ResponseDTO> saveEditPromotion(@RequestBody PromotionDTO promotionDTO) {
        ResponseDTO responseDTO = new ResponseDTO();
        Promotion promotion = promotionService.updatePromotion(
                promotionDTO.getPromotionId(),
                promotionDTO.getDetail(),
                promotionDTO.getDiscountLevel(),
                promotionDTO.getEndTime(),
                promotionDTO.getStartTime(),
                promotionDTO.getTitle(),
                promotionDTO.getCode());
        responseDTO.setMessage("Get success");
        responseDTO.setData(promotion);
        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }

}
