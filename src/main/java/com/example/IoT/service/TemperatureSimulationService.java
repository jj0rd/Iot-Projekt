package com.example.IoT.service;

import com.example.IoT.model.Device;
import com.example.IoT.repository.DeviceRepository;
import com.example.IoT.repository.TemperatureRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class TemperatureSimulationService {
    private final TemperatureService temperatureService;
    private final DeviceRepository deviceRepository;
    private final Random random = new Random();

    public TemperatureSimulationService(TemperatureService temperatureService, DeviceRepository deviceRepository) {
        this.temperatureService = temperatureService;
        this.deviceRepository = deviceRepository;
    }

    @Scheduled(fixedRate = 10000)
    public void simulateTemperatureReadings() {
        System.out.println("SCHEDULER DZIAŁA");
        List<Device> devices = deviceRepository.findAll();

        for (Device device : devices) {
            if (!device.isActive()) continue;

            double temp = 18 + random.nextDouble() * 10;
            System.out.println("Saving temp " + temp + " for " + device.getName());

            temperatureService.saveTemperature(device.getId(), temp);
        }
    }

    // inicjalizacja 3 urządzeń
    @PostConstruct
    public void initDevices() {
        if (deviceRepository.count() == 0) {
            deviceRepository.save(Device.builder().name("sensor-1").location("Salon").build());
            deviceRepository.save(Device.builder().name("sensor-2").location("Kuchnia").build());
            deviceRepository.save(Device.builder().name("sensor-3").location("Sypialnia").build());
        }
    }
}
