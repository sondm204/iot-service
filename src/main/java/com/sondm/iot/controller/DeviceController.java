package com.sondm.iot.controller;

import com.sondm.iot.dto.IrCommandRequest;
import com.sondm.iot.dto.IrCommandResponse;
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

    @PostMapping("/{deviceId}/ir")
    public ResponseEntity<IrCommandResponse> controlTV(
        @PathVariable String deviceId,
        @RequestBody(required = false) IrCommandRequest request
    ) {
        IrCommandResponse response = deviceService.controlTV(deviceId, request);
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
