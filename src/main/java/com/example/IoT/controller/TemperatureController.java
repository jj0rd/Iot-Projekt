//package com.example.IoT.controller;
//
//import com.example.IoT.model.Temperature;
//import com.example.IoT.service.TemperatureService;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//@RestController
//@RequestMapping("/temperature")
//public class TemperatureController {
//
//    private final TemperatureService service;
//
//    public TemperatureController(TemperatureService service) {
//        this.service = service;
//    }
//
//    // Zapis temperatury
//    @PostMapping
//    public ResponseEntity<Temperature> addTemperature(@RequestBody Temperature temp) {
//        temp.setTimestamp(LocalDateTime.now());
//        Temperature savedTemp = service.saveTemperature(temp.getValue());
//        return ResponseEntity.ok(savedTemp);
//    }
//
//    // Odczyt wszystkich temperatur
//    @GetMapping
//    public ResponseEntity<List<Temperature>> getAllTemperatures() {
//        List<Temperature> temps = service.getAllTemperatures();
//        return ResponseEntity.ok(temps);
//    }
//}
