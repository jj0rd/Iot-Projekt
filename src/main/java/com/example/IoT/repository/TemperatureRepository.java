package com.example.IoT.repository;

import com.example.IoT.model.Temperature;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TemperatureRepository extends JpaRepository<Temperature, Long> {

    List<Temperature> findByDeviceIdOrderByTimestampDesc(Long deviceId);
}