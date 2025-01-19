package com.tpinf4067.sale_vehicle.web;

import com.tpinf4067.sale_vehicle.service.auth.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        return ResponseEntity.ok(authService.register(user));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        String token = authService.authenticate(request.getUsername(), request.getPassword());
        return token != null ? ResponseEntity.ok(new AuthResponse(token)) : ResponseEntity.badRequest().build();
    }
}
