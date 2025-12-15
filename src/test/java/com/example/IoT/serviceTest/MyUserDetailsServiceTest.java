package com.example.IoT.serviceTest;

import com.example.IoT.repository.UserRepository;
import com.example.IoT.service.MyUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.example.IoT.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MyUserDetailsServiceTest{
    @Mock
    private UserRepository userRepository;

    private MyUserDetailsService service;

    @BeforeEach
    void setUp() {
        service = new MyUserDetailsService(userRepository);
    }

    @Test
    void loadUserByUsername_shouldReturnUserDetails() {
        User mockUser = new User();
        mockUser.setUsername("testUser");
        mockUser.setPassword("password");
        mockUser.setRoles("ROLE_USER,ROLE_ADMIN");

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(mockUser));

        UserDetails userDetails = service.loadUserByUsername("testUser");

        assertEquals("testUser", userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .collect(Collectors.toSet())
                .containsAll(Set.of("ROLE_USER","ROLE_ADMIN")));
    }

    @Test
    void loadUserByUsername_shouldThrowException_whenUserNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("unknown"));
    }
}
