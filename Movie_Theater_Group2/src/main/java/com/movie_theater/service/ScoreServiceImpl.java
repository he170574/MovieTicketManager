package com.movie_theater.service;

import com.movie_theater.entity.Account;
import com.movie_theater.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ScoreServiceImpl implements ScoreService {

    private final AccountRepository accountRepository;

    @Autowired
    public ScoreServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public void addScore(Account account, int score) {
        account.setScore(account.getScore() + score);
        accountRepository.save(account);
    }

    @Override
    public void consumeScore(Account account, int score) {
        if (account.getScore() < score) throw new RuntimeException("Not enough scores!");
        account.setScore(account.getScore() - score);
        accountRepository.save(account);
    }
}
