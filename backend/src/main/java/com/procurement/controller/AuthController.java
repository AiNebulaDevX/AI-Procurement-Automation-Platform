package com.procurement.controller;

import com.procurement.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://frontend:3000"})
public class AuthController {
    
    private final AuthenticationService authenticationService;
    
    @PostMapping("/register")
    public ResponseEntity<AuthenticationService.AuthenticationResponse> register(
            @RequestBody AuthenticationService.RegisterRequest request) {
        return ResponseEntity.ok(authenticationService.register(request));
    }
    
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationService.AuthenticationResponse> authenticate(
            @RequestBody AuthenticationService.AuthenticationRequest request) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }
}
