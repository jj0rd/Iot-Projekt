package com.example.IoT.controller;

import com.example.IoT.model.RefreshToken;
import com.example.IoT.model.User;
import com.example.IoT.repository.UserRepository;
import com.example.IoT.service.JwtService;
import com.example.IoT.service.LogService;
import com.example.IoT.service.LogoutService;
import com.example.IoT.service.MyUserDetailsService;
import com.example.IoT.repository.RefreshTokenRepository;
import lombok.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final MyUserDetailsService userDetailsService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final LogService logService;
    @Autowired
    private LogoutService logoutService;

    @Data static class AuthRequest { private String username; private String password; }
    @Data @AllArgsConstructor static class AuthResponse { private String token; }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthRequest req) {
        if (userRepository.findByUsername(req.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already taken");
        }
        User u = User.builder()
                .username(req.getUsername())
                .password(passwordEncoder.encode(req.getPassword()))
                .roles("ROLE_USER")
                .build();
        userRepository.save(u);
        String token = jwtService.generateToken(u.getUsername());
        return ResponseEntity.ok("Register successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest req) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
            );
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
        String accessToken = jwtService.generateToken(req.getUsername());
        String refreshToken = jwtService.generateRefreshToken(req.getUsername());

        logService.log(req.getUsername(), "LOGIN", "User logged in");

        RefreshToken rt = new RefreshToken();
        rt.setToken(refreshToken);
        rt.setUsername(req.getUsername());
        rt.setExpiryDate(new Date(System.currentTimeMillis() + jwtService.getRefreshExpirationMs()));
        refreshTokenRepository.save(rt);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);

        return ResponseEntity.ok(tokens);
    }
    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            @RequestHeader("Authorization") String accessHeader,
            @RequestBody(required = false) Map<String, String> body) {

        if (accessHeader == null || !accessHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Missing or invalid Authorization header");
        }

        String accessToken = accessHeader.substring(7);
        String refreshToken = body != null ? body.get("refreshToken") : null;

        logoutService.logout(accessToken, refreshToken);

        return ResponseEntity.ok("User logged out successfully");
    }
    @PostMapping("/logoutAll")
    public ResponseEntity<String> logoutAll(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7); // usuń "Bearer "
        String username = jwtService.extractUsername(token); // pobierz username z tokena

        logoutService.logoutAllSessions(username);
        return ResponseEntity.ok("Logged out all sessions");
    }
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");

        // szukamy tokena w bazie
        Optional<RefreshToken> tokenEntity = refreshTokenRepository.findByToken(refreshToken);
        if (tokenEntity.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // sprawdzamy datę ważności
        if (tokenEntity.get().getExpiryDate().before(new Date())) {
            refreshTokenRepository.delete(tokenEntity.get());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = tokenEntity.get().getUsername();
        var userDetails = userDetailsService.loadUserByUsername(username);
        String newAccessToken = jwtService.generateToken(userDetails.getUsername());

        logService.log(username, "REFRESH_TOKEN", "Access token refreshed");

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", newAccessToken);
        tokens.put("refreshToken", refreshToken); // możesz odnowić też refresh token, jeśli chcesz

        return ResponseEntity.ok(tokens);
    }

}
