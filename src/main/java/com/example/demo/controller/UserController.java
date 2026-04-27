package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtUtil;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*") // Allows all origins
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, String> body) {
        String username = body.get("username") != null ? body.get("username").trim() : null;
        String email = body.get("email") != null ? body.get("email").trim() : null;
        String password = body.get("password") != null ? body.get("password").trim() : null;

        if (password == null || password.length() < 6) {
            Map<String, Object> error = new HashMap<>();
            error.put("ok", false);
            error.put("error", "Password must be at least 6 characters.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        if (userRepository.findByUsernameIgnoreCase(username).isPresent() || userRepository.findByEmailIgnoreCase(email).isPresent()) {
            Map<String, Object> error = new HashMap<>();
            error.put("ok", false);
            error.put("error", "Username or email already registered.");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }

        String captchaAnswer = body.get("captchaAnswer") != null ? body.get("captchaAnswer").trim() : null;
        String captchaToken = body.get("captchaToken") != null ? body.get("captchaToken").trim() : null;

        if (captchaAnswer == null || captchaToken == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("ok", false);
            error.put("error", "Captcha is required.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        try {
            io.jsonwebtoken.Claims claims = jwtUtil.extractAllClaims(captchaToken);
            String tokenAnswer = claims.get("answer", String.class);
            if (!captchaAnswer.equalsIgnoreCase(tokenAnswer)) {
                Map<String, Object> error = new HashMap<>();
                error.put("ok", false);
                error.put("error", "Incorrect CAPTCHA answer.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("ok", false);
            error.put("error", "Invalid or expired CAPTCHA.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
        String role = body.get("role");
        userService.registerUser(username, email, password, role);
        Map<String, Object> response = new HashMap<>();
        response.put("ok", true);
        response.put("message", "Registration successful. Check your email for the OTP.");
        response.put("otpExpiresInSeconds", 120);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        String identity = body.get("username") != null ? body.get("username") : body.get("email");
        String password = body.get("password");

        Optional<User> userOpt = userRepository.findByUsernameIgnoreCaseOrEmailIgnoreCase(identity, identity);
        
        if (userOpt.isPresent() && userService.checkPassword(password, userOpt.get().getPassword())) {
            User user = userOpt.get();
            if (!user.getVerified()) {
                Map<String, Object> err = new HashMap<>();
                err.put("ok", false);
                err.put("code", "EMAIL_NOT_VERIFIED");
                err.put("error", "Please verify your email before logging in.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(err);
            }

            String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getEmail(), user.getRole());
            
            Map<String, Object> response = new HashMap<>();
            response.put("ok", true);
            response.put("token", token);
            response.put("id", user.getId());
            response.put("username", user.getUsername());
            response.put("email", user.getEmail());
            response.put("role", user.getRole());
            response.put("points", user.getPoints());
            return ResponseEntity.ok(response);
        }

        Map<String, Object> error = new HashMap<>();
        error.put("ok", false);
        error.put("error", "Invalid username/email or password.");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<Map<String, Object>> verifyOtp(@RequestBody Map<String, String> body) {
        String email = body.get("email") != null ? body.get("email").trim() : "";
        String otp = body.get("otp") != null ? body.get("otp").trim() : "";
        boolean isValid = userService.verifyOtp(email, otp);
        Map<String, Object> response = new HashMap<>();
        if (isValid) {
            response.put("ok", true);
            response.put("message", "Email verified successfully. You can now log in.");
            return ResponseEntity.ok(response);
        }
        response.put("ok", false);
        response.put("error", "Invalid or expired OTP.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<Map<String, Object>> resendOtp(@RequestBody Map<String, String> body) {
        String email = body.get("email") != null ? body.get("email").trim() : "";
        userService.resendOtp(email);
        Map<String, Object> response = new HashMap<>();
        response.put("ok", true);
        response.put("message", "A new OTP has been sent to your email.");
        response.put("otpExpiresInSeconds", 120);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, Object>> forgotPassword(@RequestBody Map<String, String> body) {
        userService.initiateForgotPassword(body.get("email"));
        Map<String, Object> response = new HashMap<>();
        response.put("ok", true);
        response.put("message", "If an account exists, an OTP has been sent.");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, Object>> resetPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email") != null ? body.get("email").trim() : "";
        String otp = body.get("otp") != null ? body.get("otp").trim() : "";
        String password = body.get("password") != null ? body.get("password").trim() : null;

        if (password == null || password.length() < 6) {
            Map<String, Object> error = new HashMap<>();
            error.put("ok", false);
            error.put("error", "Password must be at least 6 characters.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        boolean success = userService.resetPassword(email, otp, password);
        Map<String, Object> response = new HashMap<>();
        if (success) {
            response.put("ok", true);
            response.put("message", "Password reset successfully.");
            return ResponseEntity.ok(response);
        }
        response.put("ok", false);
        response.put("error", "Invalid OTP or email.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/add-exp")
    public ResponseEntity<?> addExp(@PathVariable Long id, @RequestBody Map<String, Long> payload) {
        User user = userRepository.findById(id).orElseThrow();
        Long exp = payload.get("exp");
        user.setPoints(user.getPoints() + exp.intValue());
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }
}
