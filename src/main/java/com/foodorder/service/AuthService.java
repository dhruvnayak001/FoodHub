package com.foodorder.service;

import com.foodorder.dto.*;
import com.foodorder.entity.User;
import com.foodorder.repository.UserRepository;
import com.foodorder.security.JwtUtils;
import com.foodorder.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder encoder;
    
    @Autowired
    private JwtUtils jwtUtils;
    
    public JwtResponse authenticate(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userRepository.findByEmail(userDetails.getEmail()).orElseThrow();
        
        return new JwtResponse(jwt, "Bearer", userDetails.getId(), userDetails.getEmail(),
                userDetails.getFullName(), user.getRole().name());
    }
    
    public void register(SignupRequest signupRequest) {
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new RuntimeException("Error: Email is already taken!");
        }
        
        User user = new User();
        user.setEmail(signupRequest.getEmail());
        user.setPassword(encoder.encode(signupRequest.getPassword()));
        user.setFullName(signupRequest.getFullName());
        user.setPhone(signupRequest.getPhone());
        user.setAddress(signupRequest.getAddress());
        user.setRole(User.Role.CUSTOMER);
        
        userRepository.save(user);
    }
    
    public User getCurrentUser(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> 
            new RuntimeException("User not found"));
    }
}
