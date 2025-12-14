package com.example.IoT.DTO;

import java.time.LocalDateTime;

public record TemperatureDto (
        Double value,
        LocalDateTime timestamp
){}

