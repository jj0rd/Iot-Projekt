package com.example.IoT.service;

import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.Set;

@Service
public class LogoutService {

    // tymczasowa "czarna lista" — w prawdziwej aplikacji można to trzymać w bazie danych lub Redisie
    private final Set<String> blacklistedTokens = new HashSet<>();

    public void logout(String token) {
        blacklistedTokens.add(token);
    }

    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }
}
