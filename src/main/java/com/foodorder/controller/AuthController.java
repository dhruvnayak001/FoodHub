package com.foodorder.controller;

import com.foodorder.dto.JwtResponse;
import com.foodorder.dto.LoginRequest;
import com.foodorder.dto.SignupRequest;
import com.foodorder.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            JwtResponse response = authService.authenticate(loginRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace(); // 🔥 ADD THIS

            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage()); // 🔥 SHOW REAL ERROR
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest signupRequest) {
        try {
            authService.register(signupRequest);
            Map<String, String> success = new HashMap<>();
            success.put("message", "User registered successfully");
            return ResponseEntity.ok(success);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
