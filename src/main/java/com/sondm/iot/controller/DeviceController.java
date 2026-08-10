package com.sondm.iot.controller;

import com.sondm.iot.config.LightCommand;
import com.sondm.iot.dto.DeviceResponse;
import com.sondm.iot.service.DeviceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/devices")
@CrossOrigin(origins = "*")
public class DeviceController {

    private final DeviceService deviceService;

    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @PostMapping("/{deviceId}/light")
    public ResponseEntity<DeviceResponse> controlLight(
        @PathVariable String deviceId,
        @RequestBody LightCommand request
    ) {
        DeviceResponse response =
            deviceService.controlLight(
                deviceId,
                request.state()
            );

        return ResponseEntity.ok(response);
    }
}
