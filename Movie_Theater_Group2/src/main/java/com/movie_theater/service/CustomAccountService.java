package com.movie_theater.service;

import com.movie_theater.dto.AccountDTO;
import com.movie_theater.entity.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.util.Streamable;

import java.util.List;
import java.util.Map;

public interface CustomAccountService {
    List<AccountDTO> convertAccountsToDTO(Streamable<Account> accounts);

    Page<Account> getAllAccounts(int pageNumber, int pageSize, Map<String, Object> filter);

    Map<String, Object> createAccount(AccountDTO account);

    Account convertDTOtoAccount(AccountDTO accountDTO);

    String activateAccount(String secret);

    Map<String, Object> updateAccount(AccountDTO accountDTO, String currentUsername);

    AccountDTO convertAccountToDTO(Account account);

    Account getAccountById(Integer id);

    void deactivateAccount(Integer id, String currentUsername);

    void activateAccount(Integer id);

    String generateUsername(String fullName);
}
