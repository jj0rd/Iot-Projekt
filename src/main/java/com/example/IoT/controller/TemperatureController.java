package com.example.IoT.controller;

import com.example.IoT.model.Temperature;
import com.example.IoT.service.LogService;
import com.example.IoT.service.TemperatureService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/temperature")
public class TemperatureController {

    private final TemperatureService service;
    private final LogService logService;

    public TemperatureController(TemperatureService service, LogService logService) {
        this.service = service;
        this.logService = logService;
    }

    // Zapis temperatury
    @PostMapping
    public ResponseEntity<Temperature> addTemperature(
            @RequestBody Temperature temp,
            Authentication authentication
    ) {
        Temperature savedTemp = service.saveTemperature(temp.getValue());

        String username = authentication.getName();

        logService.log(
                username,
                "ADD_TEMPERATURE",
                "Value=" + temp.getValue()
        );

        return ResponseEntity.ok(savedTemp);
    }

    // Odczyt wszystkich temperatur
    @GetMapping
    public ResponseEntity<List<Temperature>> getAllTemperatures(Authentication authentication) {

        List<Temperature> temps = service.getAllTemperatures();

        logService.log(
                authentication.getName(),
                "READ_TEMPERATURES",
                "Returned " + temps.size() + " records"
        );

        return ResponseEntity.ok(temps);
    }
}
