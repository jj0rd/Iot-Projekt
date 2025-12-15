package com.example.IoT.serviceTest;

import com.example.IoT.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() throws Exception {
        jwtService = new JwtService();

        // 64-znakowy klucz (512-bit) – bezpieczny dla HS512
        String secureKey = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";

        java.lang.reflect.Field secretField = JwtService.class.getDeclaredField("jwtSecret");
        secretField.setAccessible(true);
        secretField.set(jwtService, secureKey);

        java.lang.reflect.Field expField = JwtService.class.getDeclaredField("jwtExpirationMs");
        expField.setAccessible(true);
        expField.set(jwtService, 1000 * 60 * 60L); // 1 godzina

        java.lang.reflect.Field refreshExpField = JwtService.class.getDeclaredField("jwtRefreshExpirationMs");
        refreshExpField.setAccessible(true);
        refreshExpField.set(jwtService, 1000 * 60 * 60 * 24L); // 1 dzień
    }

    @Test
    void generateToken_and_extractUsername_shouldWork() {
        String username = "testUser";
        String token = jwtService.generateToken(username);

        assertNotNull(token);
        String extractedUsername = jwtService.extractUsername(token);
        assertEquals(username, extractedUsername);
    }

    @Test
    void generateRefreshToken_shouldWork() {
        String username = "refreshUser";
        String refreshToken = jwtService.generateRefreshToken(username);

        assertNotNull(refreshToken);
        String extractedUsername = jwtService.extractUsername(refreshToken);
        assertEquals(username, extractedUsername);
    }

    @Test
    void validateToken_shouldReturnTrueForValidToken() {
        String token = jwtService.generateToken("validUser");

        assertTrue(jwtService.validateToken(token));
    }

    @Test
    void validateToken_shouldReturnFalseForInvalidToken() {
        String invalidToken = "invalid.token.here";

        assertFalse(jwtService.validateToken(invalidToken));
    }

    @Test
    void getRefreshExpirationMs_shouldReturnConfiguredValue() {
        assertEquals(1000 * 60 * 60 * 24, jwtService.getRefreshExpirationMs());
    }
}
