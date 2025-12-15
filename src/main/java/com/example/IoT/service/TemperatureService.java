package com.example.IoT.service;

import com.example.IoT.model.Device;
import com.example.IoT.model.Temperature;
import com.example.IoT.repository.DeviceRepository;
import com.example.IoT.repository.TemperatureRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TemperatureService {

    private final TemperatureRepository repository;
    private final DeviceRepository deviceRepository;
    private final LogService logService;

    public TemperatureService(TemperatureRepository repository, DeviceRepository deviceRepository, LogService logService) {
        this.repository = repository;
        this.deviceRepository = deviceRepository;
        this.logService = logService;
    }

    public Temperature saveTemperature(Long deviceId, Double value) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Device not found"));

        Temperature temp = Temperature.builder()
                .value(value)
                .timestamp(LocalDateTime.now())
                .device(device)
                .build();

        Temperature saved = repository.save(temp);

        logService.log(
                getUsername(),
                "ADD_TEMPERATURE",
                "DeviceId=" + deviceId + ", value=" + value
        );

        return saved;
    }

    public List<Temperature> getAllTemperatures() {
        List<Temperature> temps = repository.findAll();

        logService.log(
                getUsername(),
                "READ_ALL_TEMPERATURES",
                "Returned " + temps.size() + " records"
        );

        return temps;
    }
    private String getUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "SYSTEM";
    }
}
