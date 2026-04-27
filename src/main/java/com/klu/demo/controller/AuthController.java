package com.klu.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.klu.demo.dto.AuthResponse;
import com.klu.demo.dto.LoginRequest;
import com.klu.demo.dto.VerifyOtpRequest;
import com.klu.demo.dto.RegisterRequest;
import com.klu.demo.entity.User;
import com.klu.demo.security.JwtUtil;
import com.klu.demo.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        User savedUser = authService.register(req);
        AuthResponse response = new AuthResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getRole(),
                savedUser.isVerified()
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            User user = authService.login(request.getEmail(), request.getPassword());

            if (user == null) {
                return ResponseEntity.status(401).body("Invalid email or password");
            }

            String token = jwtUtil.generateToken(user.getEmail());

            AuthResponse response = new AuthResponse(
                    user.getId(),
                    user.getName(),
                    user.getRole(),
                    user.isVerified(),
                    token
            );

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyOtp(@RequestBody VerifyOtpRequest request) {
        String result = authService.verifyOtp(request.getEmail(), request.getOtp());
        if ("Verified successfully".equalsIgnoreCase(result) || "Already verified".equalsIgnoreCase(result)) {
            return ResponseEntity.ok(result);
        }
        if ("User not found".equalsIgnoreCase(result)) {
            return ResponseEntity.status(404).body(result);
        }
        return ResponseEntity.badRequest().body(result);
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<?> resendOtp(@RequestBody LoginRequest request) {
        String result = authService.resendOtp(request.getEmail());
        if ("User not found".equalsIgnoreCase(result)) {
            return ResponseEntity.status(404).body(result);
        }
        return ResponseEntity.ok(result);
    }
}

