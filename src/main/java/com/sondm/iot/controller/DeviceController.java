package com.sondm.iot.controller;

import com.sondm.iot.config.LightCommand;
import com.sondm.iot.dto.DeviceResponse;
import com.sondm.iot.service.DeviceEventStreamService;
import com.sondm.iot.service.DeviceService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/devices")
@CrossOrigin(origins = "*")
public class DeviceController {

    private final DeviceService deviceService;
    private final DeviceEventStreamService deviceEventStreamService;

    public DeviceController(
        DeviceService deviceService,
        DeviceEventStreamService deviceEventStreamService
    ) {
        this.deviceService = deviceService;
        this.deviceEventStreamService = deviceEventStreamService;
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

    @PostMapping("/{deviceId}/ir")
    public ResponseEntity<DeviceResponse> controlTV(@PathVariable String deviceId) {
        DeviceResponse response = deviceService.controlTV(deviceId);
        return ResponseEntity.ok(response);
    }

    @GetMapping(
        value = "/{deviceId}/events",
        produces = MediaType.TEXT_EVENT_STREAM_VALUE
    )
    public SseEmitter streamDeviceEvents(@PathVariable String deviceId) {
        return deviceEventStreamService.subscribe();
    }
}
