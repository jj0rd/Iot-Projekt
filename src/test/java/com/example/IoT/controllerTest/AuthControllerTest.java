package com.example.IoT.controllerTest;

import com.example.IoT.controller.AuthController;
import com.example.IoT.model.RefreshToken;
import com.example.IoT.model.User;
import com.example.IoT.repository.RefreshTokenRepository;
import com.example.IoT.repository.UserRepository;
import com.example.IoT.service.JwtService;
import com.example.IoT.service.LogService;
import com.example.IoT.service.LogoutService;
import com.example.IoT.service.MyUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean private UserRepository userRepository;
    @MockBean private PasswordEncoder passwordEncoder;
    @MockBean private AuthenticationManager authenticationManager;
    @MockBean private JwtService jwtService;
    @MockBean private MyUserDetailsService userDetailsService;
    @MockBean private RefreshTokenRepository refreshTokenRepository;
    @MockBean private LogService logService;
    @MockBean private LogoutService logoutService;

    /* ================= REGISTER ================= */

    @Test
    void register_shouldReturnBadRequest_whenUsernameExists() throws Exception {
        when(userRepository.findByUsername("test"))
                .thenReturn(Optional.of(new User()));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"test","password":"123"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Username already taken"));
    }

    @Test
    void register_shouldReturnToken_whenSuccess() throws Exception {
        when(userRepository.findByUsername("test"))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode("123")).thenReturn("encoded");
        when(jwtService.generateToken("test")).thenReturn("jwt-token");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"test","password":"123"}
                                """))
                .andExpect(status().isOk());

        verify(userRepository).save(any(User.class));
    }

    /* ================= LOGIN ================= */

    @Test
    void login_shouldReturnUnauthorized_whenBadCredentials() throws Exception {
        doThrow(new BadCredentialsException("bad"))
                .when(authenticationManager)
                .authenticate(any());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"test","password":"wrong"}
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid credentials"));
    }

    @Test
    void login_shouldReturnAccessAndRefreshToken_whenSuccess() throws Exception {
        when(jwtService.generateToken("test")).thenReturn("access");
        when(jwtService.generateRefreshToken("test")).thenReturn("refresh");
        when(jwtService.getRefreshExpirationMs()).thenReturn(100000L);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"test","password":"123"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access"))
                .andExpect(jsonPath("$.refreshToken").value("refresh"));

        verify(refreshTokenRepository).save(any(RefreshToken.class));
        verify(logService).log(eq("test"), eq("LOGIN"), any());
    }

    /* ================= LOGOUT ================= */

    @Test
    void logout_shouldReturnBadRequest_whenNoBearerToken() throws Exception {
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void logout_shouldReturnOk_whenSuccess() throws Exception {
        mockMvc.perform(post("/api/auth/logout")
                        .header("Authorization", "Bearer accessToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"refreshToken":"refresh"}
                                """))
                .andExpect(status().isOk())
                .andExpect(content().string("User logged out successfully"));

        verify(logoutService).logout("accessToken", "refresh");
    }

    /* ================= REFRESH TOKEN ================= */

    @Test
    void refresh_shouldReturnUnauthorized_whenTokenNotFound() throws Exception {
        when(refreshTokenRepository.findByToken("refresh"))
                .thenReturn(Optional.empty());

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"refreshToken":"refresh"}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void refresh_shouldReturnNewAccessToken_whenValid() throws Exception {
        RefreshToken rt = new RefreshToken();
        rt.setToken("refresh");
        rt.setUsername("test");
        rt.setExpiryDate(new Date(System.currentTimeMillis() + 10000));

        when(refreshTokenRepository.findByToken("refresh"))
                .thenReturn(Optional.of(rt));
        when(userDetailsService.loadUserByUsername("test"))
                .thenReturn(new org.springframework.security.core.userdetails.User(
                        "test", "pass", List.of()));
        when(jwtService.generateToken("test")).thenReturn("newAccess");

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"refreshToken":"refresh"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("newAccess"))
                .andExpect(jsonPath("$.refreshToken").value("refresh"));

        verify(logService).log(eq("test"), eq("REFRESH_TOKEN"), any());
    }
}
