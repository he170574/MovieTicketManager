package com.movie_theater.controller;

import com.movie_theater.dto.PayDTO;
import com.movie_theater.dto.ResponseDTO;
import com.movie_theater.dto.SeatDTO;
import com.movie_theater.entity.Account;
import com.movie_theater.entity.Invoice;
import org.springframework.security.core.Authentication;
import com.movie_theater.entity.ScheduleSeatHis;
import com.movie_theater.final_attribute.STATUS;
import com.movie_theater.security.CustomAccount;
import com.movie_theater.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PayController {

    AccountService accountService;
    private PayService payService;
    private InvoiceService invoiceService;
    private ScheduleSeatService scheduleSeatService;
    private ScheduleSeatHisService scheduleSeatHisService;
    @Autowired
    public PayController(AccountService accountService,PayService payService, InvoiceService invoiceService, ScheduleSeatService scheduleSeatService, ScheduleSeatHisService scheduleSeatHisService) {
        this.accountService = accountService;
        this.payService = payService;
        this.invoiceService = invoiceService;
        this.scheduleSeatService = scheduleSeatService;
        this.scheduleSeatHisService = scheduleSeatHisService;
    }

    @PostMapping ("/payment-checking")
    public ResponseEntity<ResponseDTO> isCompletePayment(@RequestParam String transactionCode, @RequestParam Long amount, @RequestParam String invoiceId, Authentication authentication) {
        ResponseDTO responseDTO = new ResponseDTO();
        CustomAccount customAccount = (CustomAccount) authentication.getPrincipal();
        Account account = accountService.getByUsername(customAccount.getUsername());
        String email = account.getEmail();
        boolean existed = payService.isTransactionCompleted(transactionCode, amount,email);
        responseDTO.setData(existed);
        if(existed) {
            responseDTO.setMessage("Complete");
            invoiceService.updateStatus(STATUS.CONFIRM, Integer.parseInt(invoiceId));
        } else {
            responseDTO.setMessage("Fail");
        }
        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }

    @PostMapping ("/cancel-order")
    public ResponseEntity<ResponseDTO> alterBookedSeatStatus(@RequestParam String invoiceId) {
        ResponseDTO responseDTO = new ResponseDTO();
        List<ScheduleSeatHis> scheduleSeatHisList = scheduleSeatHisService.getScheduleSeatHisByInvoiceId(Integer.parseInt(invoiceId));
        for (ScheduleSeatHis scheduleSeatHis : scheduleSeatHisList) {
            scheduleSeatService.updateScheduleSeatStatus(false, scheduleSeatHis.getScheduleSeatId());
        }
        invoiceService.updateStatus(STATUS.REFUSE, Integer.parseInt(invoiceId));
        responseDTO.setMessage("order canceled");
        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }
    @GetMapping("/get-transaction-code")
    public ResponseEntity<ResponseDTO> getTransactionCode() {
        ResponseDTO responseDTO = new ResponseDTO();
        String newTransactionCode = payService.createTransactionCode();
        while (payService.isTransactionCodeExisted(newTransactionCode)) {
            newTransactionCode = payService.createTransactionCode();
        }
//        PayDTO payDT0 = PayDTO.builder().transactionCode(newTransactionCode).build();
        responseDTO.setData(newTransactionCode);
        responseDTO.setMessage("success");
        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }
}
