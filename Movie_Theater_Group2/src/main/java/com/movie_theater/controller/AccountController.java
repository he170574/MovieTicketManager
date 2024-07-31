package com.movie_theater.controller;


import com.movie_theater.dto.AccountDTO;
import com.movie_theater.dto.ChangePasswordDTO;
import com.movie_theater.dto.ResponseDTO;
import com.movie_theater.entity.Account;
import com.movie_theater.final_attribute.ROLE;
import com.movie_theater.security.CustomAccount;
import com.movie_theater.service.AccountService;
import com.movie_theater.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
public class AccountController {
    AccountService accountService;

    RoleService roleService;

    @Autowired
    public AccountController(AccountService accountService, RoleService roleService) {
        this.accountService = accountService;
        this.roleService = roleService;
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseDTO> postRegister(@RequestBody AccountDTO accountDTO) {
        Account account = Account.builder()
                .address(accountDTO.getAddress())
                .dateOfBirth(accountDTO.getDateOfBirth())
                .email(accountDTO.getEmail())
                .fullName(accountDTO.getFullName())
                .gender(accountDTO.getGender())
                .password(accountDTO.getPassword())
                .phoneNumber(accountDTO.getPhoneNumber())
                .username(accountDTO.getUsername())
                .build();
        try {
            account.setRole(roleService.getRoleByRoleName(ROLE.MEMBER));
            accountService.save(account);
            return ResponseEntity.ok().body(ResponseDTO.builder().message("Register Success").build());
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ResponseDTO.builder().message(e.getMessage()).build());
        }
    }

    @GetMapping("/get-account-using")
    public ResponseEntity<ResponseDTO> getAccountUsing(Authentication authentication) {
        ResponseDTO responseDTO = new ResponseDTO();
        try {
            if (authentication == null) {
                responseDTO.setMessage("No Login");
            } else {

                responseDTO.setMessage("Success");
                CustomAccount customAccount = (CustomAccount) authentication.getPrincipal();
                Account account = accountService.getByUsername(customAccount.getUsername());
                AccountDTO accountDTO = new AccountDTO(
                        account.getAccountId(),
                        account.getAddress(),
                        account.getDateOfBirth(),
                        account.getEmail(),
                        account.getFullName(),
                        account.getGender(),
                        account.getImage(),
                        account.getPhoneNumber(),
                        account.getUsername(),
                        account.getRole().getRoleName(),
                        account.getScore()
                );
                responseDTO.setData(accountDTO);
            }
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            responseDTO.setMessage("Internal Server Error");
            return ResponseEntity.internalServerError().body(responseDTO);
        }

    }

    @PostMapping("/update-account")
    public ResponseEntity<ResponseDTO> updateAccount(@RequestBody AccountDTO accountDTO, Authentication authentication) {
        try {
            if (authentication == null){
                //Check Login
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseDTO.builder().message("Please Login!").build());
            }
            CustomAccount customAccount = (CustomAccount) authentication.getPrincipal();
            Account account = accountService.getByUsername(accountDTO.getUsername());

            if (   (!accountDTO.getUsername().equalsIgnoreCase(customAccount.getUsername()) ||

                    accountDTO.getRole() != null)) {
                // If update important filed, need to check
                if (customAccount.getRole().equals(ROLE.ADMIN)){
                    //Is Admin
                    account.setRole(roleService.getRoleByRoleName(accountDTO.getRole()));
                    account.setUsername(accountDTO.getUsername());
                }else{
                    //Not Admin
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseDTO.builder().message("No permission to update").build());
                }
            }

            account.setAddress(accountDTO.getAddress());
            account.setDateOfBirth(accountDTO.getDateOfBirth());
            account.setEmail(accountDTO.getEmail());
            account.setFullName(accountDTO.getFullName());
            account.setGender(accountDTO.getGender());
            account.setPhoneNumber(accountDTO.getPhoneNumber());

            accountService.updateAccount(account);
            return ResponseEntity.ok().body(ResponseDTO.builder().message("Update Success").build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ResponseDTO.builder().message(e.getMessage()).build());
        }
    }

    @PostMapping("/forgot-pass")
    public ResponseEntity<ResponseDTO> getNewPassWord(@RequestBody String email) {

        if(accountService.checkMailExist( email) == 0){
            return ResponseEntity.badRequest().body(ResponseDTO.builder().message("Send Mail fail").build());
        }
        accountService.updateNewPassWord(email);
        return ResponseEntity.ok().body(ResponseDTO.builder().message("Send Mail Success").build());
    }

    @PostMapping("/changePassword")
    public ResponseEntity<ResponseDTO> changePassWord(Authentication authentication, @RequestBody ChangePasswordDTO changePasswordDTO){
        CustomAccount account = (CustomAccount) authentication.getPrincipal();

        int numberRowEffect = accountService.updateAccountByAccountUserName(account.getUsername(), changePasswordDTO.getNewPassword(), changePasswordDTO.getOldPassword());

        if(numberRowEffect > 0){
            return ResponseEntity.ok().body(ResponseDTO.builder().message("change password success").build());
        }
        return ResponseEntity.badRequest().body(ResponseDTO.builder().message("change password fail").data("Old Password Wrong").build());
    }
}
