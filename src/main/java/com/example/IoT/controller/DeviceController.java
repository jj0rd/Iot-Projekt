package com.example.IoT.controller;

import com.example.IoT.DTO.DeviceDto;
import com.example.IoT.DTO.TemperatureDto;
import com.example.IoT.model.Device;
import com.example.IoT.model.Temperature;
import com.example.IoT.repository.DeviceRepository;
import com.example.IoT.repository.TemperatureRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/devices")
public class DeviceController {
    private final DeviceRepository deviceRepository;
    private final TemperatureRepository temperatureRepository;

    public DeviceController(DeviceRepository deviceRepository, TemperatureRepository temperatureRepository) {
        this.deviceRepository = deviceRepository;
        this.temperatureRepository = temperatureRepository;
    }

    @GetMapping("/getAllDevices")
    public List<DeviceDto> getAllDevices() {
        return deviceRepository.findAll().stream()
                .map(d -> new DeviceDto(
                        d.getId(),
                        d.getName(),
                        d.getLocation(),
                        d.isActive()
                ))
                .toList();
    }

    @GetMapping("/devices/{id}/temperatures")
    public List<TemperatureDto> getTemperatures(@PathVariable Long id) {
        return temperatureRepository.findByDeviceIdOrderByTimestampDesc(id)
                .stream()
                .map(t -> new TemperatureDto(
                        t.getValue(),
                        t.getTimestamp()
                ))
                .toList();
    }
}
