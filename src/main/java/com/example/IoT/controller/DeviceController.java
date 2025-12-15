package com.example.IoT.controller;

import com.example.IoT.DTO.DeviceDto;
import com.example.IoT.DTO.TemperatureDto;
import com.example.IoT.model.Device;
import com.example.IoT.model.Temperature;
import com.example.IoT.repository.DeviceRepository;
import com.example.IoT.repository.TemperatureRepository;
import com.example.IoT.service.LogService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;

import java.util.List;

@RestController
@RequestMapping("/devices")
public class DeviceController {
    private final DeviceRepository deviceRepository;
    private final TemperatureRepository temperatureRepository;
    private final LogService logService;

    public DeviceController(DeviceRepository deviceRepository, TemperatureRepository temperatureRepository, LogService logService) {
        this.deviceRepository = deviceRepository;
        this.temperatureRepository = temperatureRepository;
        this.logService = logService;
    }

    @GetMapping("/getAllDevices")
    public List<DeviceDto> getAllDevices(Authentication authentication) {

        List<DeviceDto> devices = deviceRepository.findAll().stream()
                .map(d -> new DeviceDto(
                        d.getId(),
                        d.getName(),
                        d.getLocation(),
                        d.isActive()
                ))
                .toList();

        logService.log(
                authentication.getName(),
                "READ_DEVICES",
                "Returned " + devices.size() + " devices"
        );

        return devices;
    }

    @GetMapping("/{id}/temperatures")
    public List<TemperatureDto> getTemperatures(
            @PathVariable Long id,
            Authentication authentication
    ) {
        List<TemperatureDto> temps = temperatureRepository
                .findByDeviceIdOrderByTimestampDesc(id)
                .stream()
                .map(t -> new TemperatureDto(
                        t.getValue(),
                        t.getTimestamp()
                ))
                .toList();

        logService.log(
                authentication.getName(),
                "READ_DEVICE_TEMPERATURES",
                "DeviceId=" + id + ", records=" + temps.size()
        );

        return temps;
    }
}
