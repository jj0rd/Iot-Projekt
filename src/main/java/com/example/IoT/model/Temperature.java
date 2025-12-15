package com.example.IoT.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "temperatures")
public class Temperature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double value; // temperatura w stopniach

    private LocalDateTime timestamp;

    @ManyToOne
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;
}
