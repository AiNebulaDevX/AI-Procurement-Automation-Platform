package com.procurement.service;

import com.procurement.entity.User;
import com.procurement.repository.UserRepository;
import com.procurement.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    
    public AuthenticationResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username)) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(request.email)) {
            throw new RuntimeException("Email already exists");
        }
        
        User user = User.builder()
                .username(request.username)
                .email(request.email)
                .passwordHash(passwordEncoder.encode(request.password))
                .role(request.role)
                .department(request.department)
                .build();
        
        userRepository.save(user);
        
        String jwtToken = jwtService.generateToken(user);
        
        return new AuthenticationResponse(jwtToken, user.getUsername(), user.getRole().name());
    }
    
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username,
                        request.password
                )
        );
        
        User user = userRepository.findByUsername(request.username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        String jwtToken = jwtService.generateToken(user);
        
        return new AuthenticationResponse(jwtToken, user.getUsername(), user.getRole().name());
    }
    
    public record AuthenticationRequest(String username, String password) {}
    public record RegisterRequest(String username, String email, String password, User.Role role, String department) {}
    public record AuthenticationResponse(String token, String username, String role) {}
}
