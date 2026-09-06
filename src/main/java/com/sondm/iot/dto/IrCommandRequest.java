package com.sondm.iot.dto;

import java.time.OffsetDateTime;

public record IrCommandRequest(
    String command,
    OffsetDateTime scheduledAt,
    Long delaySeconds
) {
}
