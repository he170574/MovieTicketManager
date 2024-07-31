package com.movie_theater.controller;

import com.movie_theater.dto.AccountDTO;
import com.movie_theater.dto.ResponseDTO;
import com.movie_theater.entity.Account;
import com.movie_theater.service.CustomAccountService;
import lombok.extern.java.Log;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Log
@Controller
public class CustomAccountController {

    private final CustomAccountService accountService;

    public CustomAccountController(CustomAccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/get-all-account")
    public ResponseEntity<ResponseDTO> getAllAccounts(@RequestParam(required = false) Optional<Integer> pageNumber,
                                                      @RequestParam(required = false) Optional<Integer> pageSize,
                                                      @RequestParam(required = false) Optional<String> fullName,
                                                      @RequestParam(required = false) Optional<String> status,
                                                      @RequestParam(required = false) Optional<String> role) {
        Map<String, Object> filter = new HashMap<>();
        fullName.ifPresent(value -> filter.put("fullName", value));
        status.ifPresent(value -> filter.put("status", value));
        role.ifPresent(value -> filter.put("role", value));

        Page<Account> allAccounts = accountService.getAllAccounts(
                pageNumber.orElse(0),
                pageSize.orElse(10),
                filter
        );
        List<AccountDTO> allAccountsDTO = accountService.convertAccountsToDTO(allAccounts);

        return ResponseEntity.ok().body(ResponseDTO.builder()
                .message("Success")
                .data(Map.of(
                        "result", allAccountsDTO,
                        "pageNumber", allAccounts.getNumber(),
                        "pageSize", allAccounts.getSize(),
                        "totalPage", allAccounts.getTotalPages(),
                        "count", allAccounts.getTotalElements()
                )).build());
    }

    @PostMapping("/activate-account-by-id")
    public ResponseEntity<ResponseDTO> activateAccountById(@RequestParam Integer id) {
        try {
            accountService.activateAccount(id);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseDTO.builder()
                    .message("Error")
                    .data(e.getMessage()).build());
        }
        return ResponseEntity.ok().body(ResponseDTO.builder().message("Activate account successfully!").build());
    }

    @GetMapping("/activate-account")
    public ModelAndView activateAccount(@RequestParam String secret) {
        String message = accountService.activateAccount(secret);
        ModelAndView mav = new ModelAndView("activateAccount");
        mav.addObject("message", message);
        return mav;
    }

    @PostMapping("/create-account")
    public ResponseEntity<ResponseDTO> createAccount(@ModelAttribute AccountDTO accountDTO) {

        Map<String, Object> result = accountService.createAccount(accountDTO);

        // Error
        if (result.containsKey("error")) {
            return ResponseEntity.badRequest().body(ResponseDTO.builder()
                    .message("Error")
                    .data(result.get("error")).build());
        }

        return ResponseEntity.ok().body(ResponseDTO.builder().message("Create new account successfully! Check Email to Activate").build());
    }

    @PostMapping("/update-account-info")
    public ResponseEntity<ResponseDTO> updateAccount(Authentication authentication, @ModelAttribute AccountDTO accountDTO) {

        Map<String, Object> result = accountService.updateAccount(accountDTO, authentication.getName());

        // Error
        if (result.containsKey("error")) {
            return ResponseEntity.badRequest().body(ResponseDTO.builder()
                    .message("Error")
                    .data(result.get("error")).build());
        }

        return ResponseEntity.ok().body(ResponseDTO.builder().message("Update account successfully!").build());
    }

    @PostMapping("/deactivate-account")
    public ResponseEntity<ResponseDTO> deactivateAccount(Authentication authentication, @RequestParam Integer id) {
        try {
            String currentUsername = authentication.getName();
            accountService.deactivateAccount(id, currentUsername);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseDTO.builder()
                    .message("Error")
                    .data(e.getMessage()).build());
        }
        return ResponseEntity.ok().body(ResponseDTO.builder().message("Deactivate account successfully!").build());
    }

    @GetMapping("/get-account-info-by-id")
    public ResponseEntity<ResponseDTO> getEmployeeByEmployeeId(@RequestParam Integer id) {
        Account account = accountService.getAccountById(id);
        AccountDTO accountDTO = accountService.convertAccountToDTO(account);
        return ResponseEntity.ok().body(ResponseDTO.builder()
                .message("Success")
                .data(accountDTO).build());
    }

    @GetMapping("/generate-username")
    public ResponseEntity<ResponseDTO> getEmployeeByEmployeeId(@RequestParam String fullName) {
        String result = accountService.generateUsername(fullName);
        return ResponseEntity.ok().body(ResponseDTO.builder()
                .message("Success")
                .data(result).build());
    }
}
