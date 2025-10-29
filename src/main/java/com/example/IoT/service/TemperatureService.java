package com.example.IoT.service;

import com.example.IoT.model.Temperature;
import com.example.IoT.repository.TemperatureRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TemperatureService {

    private final TemperatureRepository repository;

    public TemperatureService(TemperatureRepository repository) {
        this.repository = repository;
    }

    public Temperature saveTemperature(Double value) {
        Temperature temp = Temperature.builder()
                .value(value)
                .timestamp(LocalDateTime.now())
                .build();
        return repository.save(temp);
    }

    public List<Temperature> getAllTemperatures() {
        return repository.findAllByOrderByTimestampDesc();
    }
}
