package com.movie_theater.service;

import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.Random;
import java.util.regex.Pattern;

@Service
public class TextServiceImpl implements TextService {

    @Override
    public String removeAccent(String text) {
        text = text.replaceAll("[àáạảãâầấậẩẫăằắặẳẵ]", "a");
        text = text.replaceAll("[èéẹẻẽêềếệểễ]", "e");
        text = text.replaceAll("[ìíịỉĩ]", "i");
        text = text.replaceAll("[òóọỏõôồốộổỗơờớợởỡ]", "o");
        text = text.replaceAll("[ùúụủũưừứựửữ]", "u");
        text = text.replaceAll("[ỳýỵỷỹ]", "y");
        text = text.replaceAll("đ", "d");
        text = text.replaceAll("[ÀÁẠẢÃÂẦẤẬẨẪĂẰẮẶẲẴ]", "A");
        text = text.replaceAll("[ÈÉẸẺẼÊỀẾỆỂỄ]", "E");
        text = text.replaceAll("[ÌÍỊỈĨ]", "I");
        text = text.replaceAll("[ÒÓỌỎÕÔỒỐỘỔỖƠỜỚỢỞỠ]", "O");
        text = text.replaceAll("[ÙÚỤỦŨƯỪỨỰỬỮ]", "U");
        text = text.replaceAll("[ỲÝỴỶỸ]", "Y");
        text = text.replaceAll("Đ", "D");
        return text;
    }

    @Override
    public String onlyAlphabet(String text) {
        return text.replaceAll("[^a-zA-Z]", "");
    }

    @Override
    public String onlyAlphanumeric(String text) {
        return "";
    }

    @Override
    public String normalizeFullname(String fullName) {
        return onlyAlphabet(removeAccent(fullName)).toLowerCase();
    }

    @Override
    public String generateRandomPassword() {
        String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ!@#$%^&*()_+";
        Random rnd = new Random();

        StringBuilder sb = new StringBuilder(10);
        for (int i = 0; i < 10; i++) {
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        }
        return sb.toString();
    }
}
