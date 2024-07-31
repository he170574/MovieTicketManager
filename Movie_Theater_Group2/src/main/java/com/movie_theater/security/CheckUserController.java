package com.movie_theater.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class CheckUserController {


    @GetMapping("/auth")
    public String auth(Model model, Principal principal, Authentication authentication){

        return "testHTML";
    }

}
