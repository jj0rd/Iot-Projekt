package com.example.IoT.controllerTest;

import com.example.IoT.DTO.DeviceDto;
import com.example.IoT.DTO.TemperatureDto;
import com.example.IoT.controller.DeviceController;
import com.example.IoT.model.Device;
import com.example.IoT.model.Temperature;
import com.example.IoT.repository.DeviceRepository;
import com.example.IoT.repository.TemperatureRepository;
import com.example.IoT.service.LogService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DeviceController.class)
@AutoConfigureMockMvc(addFilters = false)
class DeviceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private DeviceRepository deviceRepository;
    @MockBean private TemperatureRepository temperatureRepository;
    @MockBean private LogService logService;

    @MockBean private Authentication authentication; // mock autoryzacji

    @Test
    void getAllDevices_shouldReturnDeviceList() throws Exception {
        // przygotowanie danych
        Device d1 = new Device();
        d1.setId(1L);
        d1.setName("Device1");
        d1.setLocation("Room1");
        d1.setActive(true);

        Device d2 = new Device();
        d2.setId(2L);
        d2.setName("Device2");
        d2.setLocation("Room2");
        d2.setActive(false);

        when(deviceRepository.findAll()).thenReturn(List.of(d1, d2));
        when(authentication.getName()).thenReturn("testUser");

        mockMvc.perform(get("/devices/getAllDevices")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Device1"))
                .andExpect(jsonPath("$[0].location").value("Room1"))
                .andExpect(jsonPath("$[0].active").value(true))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Device2"))
                .andExpect(jsonPath("$[1].location").value("Room2"))
                .andExpect(jsonPath("$[1].active").value(false));

        verify(logService).log(eq("testUser"), eq("READ_DEVICES"), eq("Returned 2 devices"));
    }

}
