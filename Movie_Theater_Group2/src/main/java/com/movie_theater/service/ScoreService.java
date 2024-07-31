package com.movie_theater.service;

import com.movie_theater.entity.Account;

public interface ScoreService {
    void addScore(Account account, int score);

    void consumeScore(Account account, int score);
}
