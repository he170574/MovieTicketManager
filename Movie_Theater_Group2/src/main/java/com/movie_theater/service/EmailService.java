package com.movie_theater.service;

import org.springframework.scheduling.annotation.Async;

import javax.mail.MessagingException;

public interface EmailService {
    @Async
    void sendEmail(String to, String subject, String body) throws MessagingException;

    @Async
    void sendActivateEmail(String email, String username, String secret, String randomPassword) throws MessagingException;
}
