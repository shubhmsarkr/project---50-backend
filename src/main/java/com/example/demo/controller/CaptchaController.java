package com.example.demo.controller;

import com.example.demo.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api/captcha")
@CrossOrigin(origins = "*")
public class CaptchaController {

    @Autowired
    private JwtUtil jwtUtil;

    private final Random random = new Random();

    @GetMapping
    public ResponseEntity<Map<String, String>> getCaptcha() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        String captchaText = sb.toString();

        String token = jwtUtil.generateCaptchaToken(captchaText);

        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("question", captchaText);
        return ResponseEntity.ok(response);
    }
}
