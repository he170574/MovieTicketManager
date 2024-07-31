package com.movie_theater.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.mail.MessagingException;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

    @InjectMocks
    EmailServiceImpl emailService;

    @Test
    public void testSendEmail() throws MessagingException {

        String body = """
                <html>
                <body>
                <h1>Activate your account</h1>
                <a href="http://localhost:8080/">Test</div>
                </body>
                </html>
                """;
        emailService.sendEmail("hieu05992@gmail.com", "Test Email", body);
    }
}
