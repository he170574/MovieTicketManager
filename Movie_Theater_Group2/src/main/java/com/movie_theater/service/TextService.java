package com.movie_theater.service;

public interface TextService {
    String removeAccent(String text);

    String onlyAlphabet(String text);

    String onlyAlphanumeric(String text);

    String normalizeFullname(String fullname);

    String generateRandomPassword();
}
