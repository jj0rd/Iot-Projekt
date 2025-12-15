package com.example.IoT.DTO;

public record DeviceDto (
    Long id,
    String name,
    String location,
    boolean active
){}
