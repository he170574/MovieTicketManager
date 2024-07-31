package com.movie_theater.controller;

import com.movie_theater.dto.AccountDTO;
import com.movie_theater.dto.BookingListDTO;
import com.movie_theater.dto.ResponseDTO;
import com.movie_theater.entity.Account;
import com.movie_theater.final_attribute.ROLE;
import com.movie_theater.security.CustomAccount;
import com.movie_theater.service.AccountService;
import com.movie_theater.service.BookingListService;
import com.movie_theater.service.UploadImage;
import com.movie_theater.service.RoleService;
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
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class EmployeeController {

    private AccountService accountService;

    private RoleService roleService;

    private BookingListService bookingListService;

    @Autowired
    public EmployeeController(BookingListService bookingListService, AccountService accountService, RoleService roleService) {
        this.bookingListService = bookingListService;
        this.accountService = accountService;
        this.roleService = roleService;
    }

    @GetMapping("/get-booking-list")
    public ResponseEntity<ResponseDTO> getBookingList(Authentication authentication,
                                                      @RequestParam Integer pageNumber,
                                                      @RequestParam Integer pageSize,
                                                      @RequestParam Integer searchByInvoiceId) {
        List<BookingListDTO> bl = bookingListService.findActiveBookings();
        HashMap<String, Object> mapBookListDTo = new HashMap<>();
        mapBookListDTo.put("lstBookingTicket", bl);
        mapBookListDTo.put("pageNumber", pageNumber);
        mapBookListDTo.put("pageSize", pageSize);
        mapBookListDTo.put("totalPage", 5);

        return ResponseEntity.ok().body(ResponseDTO.builder().message("okela").data(mapBookListDTo).build());

    }

    @GetMapping("/get-all-employee")
    public ResponseEntity<ResponseDTO> getAllEmployee(Authentication authentication,
                                                      @RequestParam Integer pageNumber,
                                                      @RequestParam Integer pageSize,
                                                      @RequestParam String fullName) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseDTO.builder().message("Login before").build());
        }
        if (!((CustomAccount) authentication.getPrincipal()).getRole().equals(ROLE.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseDTO.builder().message("Don't have permission").build());
        }
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Account> pageEmployee = accountService.getAllEmployee(fullName, pageable);

        List<AccountDTO> lstEmployee = pageEmployee.stream().map(item -> AccountDTO.builder()
                        .accountId(item.getAccountId())
                        .fullName(item.getFullName())
                        .dateOfBirth(item.getDateOfBirth())
                        .email(item.getEmail())
                        .phoneNumber(item.getPhoneNumber())
                        .address(item.getAddress())
                        .username(item.getUsername())
                        .registerDate(item.getRegisterDate())
                        .gender(item.getGender())
                        .build())
                .toList();
        HashMap<String, Object> mapEmployee = new HashMap<>();
        mapEmployee.put("lstEmployee", lstEmployee);
        mapEmployee.put("pageNumber", pageEmployee.getNumber());
        mapEmployee.put("pageSize", pageEmployee.getSize());
        mapEmployee.put("totalPage", pageEmployee.getTotalPages());

        return ResponseEntity.ok().body(ResponseDTO.builder().
                message("Success").
                data(mapEmployee).
                build());
    }

    @GetMapping("/get-employee-by-id")
    public ResponseEntity<ResponseDTO> getEmployeeByEmployeeId(Authentication authentication,
                                                               @RequestParam Integer employeeId) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseDTO.builder().message("Login before").build());
        }
        if (!((CustomAccount) authentication.getPrincipal()).getRole().equals(ROLE.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseDTO.builder().message("Don't have permission").build());
        }
        Account item = accountService.getAccountByAccountId(employeeId);

        AccountDTO employeeDTO = AccountDTO.builder()
                .accountId(item.getAccountId())
                .fullName(item.getFullName())
                .dateOfBirth(item.getDateOfBirth())
                .email(item.getEmail())
                .phoneNumber(item.getPhoneNumber())
                .address(item.getAddress())
                .username(item.getUsername())
                .registerDate(item.getRegisterDate())
                .gender(item.getGender())
                .image(item.getImage())
                .build();

        return ResponseEntity.ok().body(ResponseDTO.builder().
                message("Success").
                data(employeeDTO).
                build());
    }

    @PostMapping("/delete-employee-by-id")
    public ResponseEntity<ResponseDTO> deleteEmployeeByEmployeeId(Authentication authentication,
                                                                  @RequestParam Integer employeeId) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseDTO.builder().message("Login before").build());
        }
        if (!((CustomAccount) authentication.getPrincipal()).getRole().equals(ROLE.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseDTO.builder().message("Don't have permission").build());
        }

        Account account = accountService.getAccountByAccountId(employeeId);

        if (account == null) {
            return ResponseEntity.badRequest().body(ResponseDTO.builder().message("Does not exit employee id").build());
        }
        accountService.deleteEmployee(account);

        return ResponseEntity.ok().body(ResponseDTO.builder().
                message("Success").
                data("Delete Success").
                build());
    }

    @PostMapping("/add-employee")
    public ResponseEntity<ResponseDTO> addEmployee(@ModelAttribute AccountDTO accountDTO, Authentication authentication) throws IOException {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseDTO.builder().message("Login before").build());
        }

        if (!((CustomAccount) authentication.getPrincipal()).getRole().equals(ROLE.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseDTO.builder().message("Don't have permission").build());
        }
        //Add validate here
        if (accountDTO.getDateOfBirth().isAfter(LocalDate.now())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseDTO.builder().message("Invalid Date Of birth").build());
        }

        //Upload Image
        // Tạo một file tạm thời
        File file = File.createTempFile("temp", null);
        // Chuyển dữ liệu từ MultipartFile sang File
        accountDTO.getImgFile().transferTo(file);
        if (!accountDTO.getImgFile().isEmpty()) {
            String imageUrl = UploadImage.upload(file);

            if (imageUrl == null) {
                return ResponseEntity.badRequest().body(ResponseDTO.builder().message("Can't update image now").build());
            }

            accountDTO.setImage(imageUrl);
        }


        Account account = Account.builder()
                .username(accountDTO.getUsername())
                .password(accountDTO.getPassword())
                .fullName(accountDTO.getFullName())
                .gender(accountDTO.getGender())
                .dateOfBirth(accountDTO.getDateOfBirth())
                .email(accountDTO.getEmail())
                .address(accountDTO.getAddress())
                .phoneNumber(accountDTO.getPhoneNumber())
                .image(accountDTO.getImage())
                .role(roleService.getRoleByRoleName(ROLE.EMPLOYEE))
                .build();
        Account accountSaved = accountService.save(account);
        if (accountSaved != null) {
            accountService.sentEmailToEmp(accountDTO.getPassword(), account.getUsername(), account.getEmail());
            return ResponseEntity.ok().body(ResponseDTO.builder().message("Add New Success").build());
        }
        return ResponseEntity.badRequest().body(ResponseDTO.builder().message("Internal Error. PLease try later").build());
    }

    @PostMapping("/update-employee")
    public ResponseEntity<ResponseDTO> updateEmployee(@ModelAttribute AccountDTO accountDTO, Authentication authentication) throws IOException {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseDTO.builder().message("Login before").build());
        }

        if (!((CustomAccount) authentication.getPrincipal()).getRole().equals(ROLE.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseDTO.builder().message("Don't have permission").build());
        }
        //Add validate here
        if (accountDTO.getDateOfBirth().isAfter(LocalDate.now())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseDTO.builder().message("Invalid Date Of birth").build());
        }

        //Upload Image

        // Tạo một file tạm thời
        File file = File.createTempFile("temp", null);
        // Chuyển dữ liệu từ MultipartFile sang File
        accountDTO.getImgFile().transferTo(file);


        if (file.length() > 0) {
            String imageUrl = UploadImage.upload(file);
            if (imageUrl == null) {
                return ResponseEntity.badRequest().body(ResponseDTO.builder().message("Can't update image now").build());
            }
            accountDTO.setImage(imageUrl);
        }

        Account account = accountService.getByUsername(accountDTO.getUsername());
        account.setUsername(accountDTO.getUsername());
        if (!accountDTO.getPassword().equals("$$$YOU_CAN_NOT_SEE_PASSWORD$$$")) {
            account.setPassword(accountDTO.getPassword());
        }
        account.setFullName(accountDTO.getFullName());
        account.setGender(accountDTO.getGender());
        account.setDateOfBirth(accountDTO.getDateOfBirth());
        account.setEmail(accountDTO.getEmail());
        account.setAddress(accountDTO.getAddress());
        account.setPhoneNumber(accountDTO.getPhoneNumber());
        account.setImage(accountDTO.getImage() == null ? account.getImage() : accountDTO.getImage());

        Account accountSaved = accountService.save(account);
        if (accountSaved != null) {
            accountService.sentEmailToEmp(accountDTO.getPassword(), account.getUsername(), account.getEmail());
            return ResponseEntity.ok().body(ResponseDTO.builder().message("Add New Success").build());
        }
        return ResponseEntity.badRequest().body(ResponseDTO.builder().message("Internal Error. PLease try later").build());
    }

    @GetMapping("/get-all-employee-inactive")
    public ResponseEntity<ResponseDTO> getAllEmployeeInactive(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseDTO.builder().message("Login before").build());
        }
        if (!((CustomAccount) authentication.getPrincipal()).getRole().equals(ROLE.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseDTO.builder().message("Don't have permission").build());
        }


        List<Account> accounts = accountService.getAccountsByDeletedTrue();

        List<AccountDTO> accountDTOs = accounts.stream().map(account -> new AccountDTO(
                account.getAccountId(),
                account.getAddress(),
                account.getDateOfBirth(),
                account.getEmail(),
                account.getFullName(),
                account.getGender(),
                account.getImage(),
                account.getPhoneNumber(),
                account.getUsername(),
                account.getRole().getRoleName(), // Giả sử Role có phương thức getRoleName() trả về tên vai trò
                account.getScore())).collect(Collectors.toList());


        return ResponseEntity.ok().body(ResponseDTO.builder().
                message("Success").
                data(accountDTOs).
                build());
    }

    @PostMapping("/active-employee-by-id")
    public ResponseEntity<ResponseDTO> activeEmployeeByEmployeeId(Authentication authentication,
                                                                  @RequestParam Integer employeeId) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseDTO.builder().message("Login before").build());
        }
        if (!((CustomAccount) authentication.getPrincipal()).getRole().equals(ROLE.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseDTO.builder().message("Don't have permission").build());
        }

        Account account = accountService.getAccountByAccountId(employeeId);

        if (account == null) {
            return ResponseEntity.badRequest().body(ResponseDTO.builder().message("Does not exit employee id").build());
        }
        accountService.activeEmployee(account);

        return ResponseEntity.ok().body(ResponseDTO.builder().
                message("Success").
                data("Active Success").
                build());
    }
}
