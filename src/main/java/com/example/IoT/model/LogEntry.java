package com.example.IoT.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;      // kto wykonał akcję

    private String action;        // LOGIN, LOGOUT, REFRESH_TOKEN, ADD_TEMPERATURE

    private String details;       // dodatkowe info

    private LocalDateTime timestamp;
}
