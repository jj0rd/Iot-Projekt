package com.example.IoT.service;

import com.example.IoT.model.Device;
import com.example.IoT.model.Temperature;
import com.example.IoT.repository.DeviceRepository;
import com.example.IoT.repository.TemperatureRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TemperatureService {

    private final TemperatureRepository repository;
    private final DeviceRepository deviceRepository;

    public TemperatureService(TemperatureRepository repository, DeviceRepository deviceRepository) {
        this.repository = repository;
        this.deviceRepository = deviceRepository;
    }

    public Temperature saveTemperature(Long deviceId, Double value) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Device not found"));

        Temperature temp = Temperature.builder()
                .value(value)
                .timestamp(LocalDateTime.now())
                .device(device)
                .build();

        return repository.save(temp);
    }
}
