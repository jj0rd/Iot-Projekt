package com.example.IoT.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.Set;
import com.example.IoT.repository.RefreshTokenRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LogoutService {

    private final Set<String> blacklistedAccessTokens = new HashSet<>();

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    // Wylogowanie jednej sesji
    public void logout(String accessToken, String refreshToken) {
        if (accessToken != null) blacklistedAccessTokens.add(accessToken);
        if (refreshToken != null && !refreshToken.isEmpty()) {
            refreshTokenRepository.deleteByToken(refreshToken);
        }
    }

    // Wylogowanie wszystkich sesji użytkownika
    @Transactional
    public void logoutAllSessions(String username) {
        // czarna lista wszystkich access tokenów nie jest w pełni możliwa bez przechowywania ich w bazie
        // ale usuwamy wszystkie refresh tokeny danego użytkownika
        refreshTokenRepository.deleteAllByUsername(username);
    }

    public boolean isAccessTokenBlacklisted(String token) {
        return blacklistedAccessTokens.contains(token);
    }

    public boolean isRefreshTokenValid(String token) {
        return refreshTokenRepository.findByToken(token).isPresent();
    }
}
