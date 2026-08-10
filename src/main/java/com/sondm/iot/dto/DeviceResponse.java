package com.sondm.iot.dto;

public record DeviceResponse(
    boolean success,
    String deviceId,
    String state,
    String topic
) {
}
