package com.sondm.iot.dto;

import java.time.OffsetDateTime;

public record IrCommandResponse(
    boolean success,
    String deviceId,
    String command,
    String topic,
    boolean scheduled,
    OffsetDateTime scheduledAt
) {
}
