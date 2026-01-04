package com.example.IoT.service;

import com.example.IoT.model.LogEntry;
import com.example.IoT.repository.LogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LogService {

    private final LogRepository logRepository;

    public LogService(LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    public void log(String username, String action, String details) {
        LogEntry log = LogEntry.builder()
                .username(username)
                .action(action)
                .details(details)
                .timestamp(LocalDateTime.now())
                .build();

        logRepository.save(log);
    }
    public List<LogEntry> getAllLogs() {
        return logRepository.findAllByOrderByTimestampDesc();
    }
}
