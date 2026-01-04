package com.example.IoT.controller;

import com.example.IoT.model.LogEntry;
import com.example.IoT.service.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class LogController {
    private final LogService logService;

    @GetMapping("/logs")
    public List<LogEntry> getLogs() {
        return logService.getAllLogs();
    }
}
