package com.movie_theater.service;

import com.movie_theater.dto.AccountDTO;
import com.movie_theater.entity.Account;
import com.movie_theater.repository.AccountRepository;
import lombok.extern.java.Log;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Log
public class AccountServiceTest {
    @InjectMocks
    private CustomAccountServiceImpl accountService;

    @Mock
    private AccountRepository accountRepository;

    @Test
    @Transactional
    public void checkCreateAccount() {
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setUsername("test");
        accountDTO.setPassword("test");
        accountDTO.setEmail("hieu05992@gmail.com");
        when(accountRepository.save(any(Account.class))).thenReturn(Account.builder().accountId(123).build());
        when(accountRepository.exists(any(Specification.class))).thenReturn(false);

        Map<String, Object> result = accountService.createAccount(accountDTO);
        Account account = (Account) result.get("data");
        log.info(result.toString());
        assert (account.getAccountId() == 123);
    }
}
