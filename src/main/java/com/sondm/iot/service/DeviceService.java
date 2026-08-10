package com.sondm.iot.service;

import com.sondm.iot.config.LightState;
import com.sondm.iot.config.MqttGateway;
import com.sondm.iot.dto.DeviceResponse;
import org.springframework.stereotype.Service;

@Service
public class DeviceService {

    private final MqttGateway mqttGateway;

    public DeviceService(MqttGateway mqttGateway) {
        this.mqttGateway = mqttGateway;
    }

    public DeviceResponse controlLight(String deviceId, LightState state) {
        String topic = String.format(
            "devices/%s/commands/light",
            deviceId
        );

        mqttGateway.publish(
            state.name(),
            topic,
            1
        );

        return new DeviceResponse(
            true,
            deviceId,
            state.name(),
            topic
        );
    }
}