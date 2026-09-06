package com.sondm.iot.service;

import com.sondm.iot.config.MqttGateway;
import com.sondm.iot.dto.IrCommandRequest;
import com.sondm.iot.dto.IrCommandResponse;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
public class DeviceService {

    private final MqttGateway mqttGateway;
    private final TaskScheduler taskScheduler;
    private final ZoneId serverZone;

    public DeviceService(
        MqttGateway mqttGateway,
        TaskScheduler taskScheduler
    ) {
        this.mqttGateway = mqttGateway;
        this.taskScheduler = taskScheduler;
        this.serverZone = ZoneId.systemDefault();
    }

    public IrCommandResponse controlTV(
        String deviceId,
        IrCommandRequest request
    ) {
        String topic = String.format(
            "devices/%s/commands/ir",
            deviceId
        );
        String command = resolveCommand(request);
        OffsetDateTime scheduledAt = resolveScheduledAt(request);

        if (scheduledAt == null) {
            publishIrCommand(command, topic);

            return new IrCommandResponse(
                true,
                deviceId,
                command,
                topic,
                false,
                null
            );
        }

        taskScheduler.schedule(
            () -> publishIrCommand(command, topic),
            scheduledAt.toInstant()
        );

        return new IrCommandResponse(
            true,
            deviceId,
            command,
            topic,
            true,
            scheduledAt
        );
    }

    private String resolveCommand(IrCommandRequest request) {
        if (
            request == null ||
                !StringUtils.hasText(request.command())
        ) {
            return "POWER";
        }

        return request.command().trim();
    }

    private OffsetDateTime resolveScheduledAt(IrCommandRequest request) {
        if (request == null) {
            return null;
        }

        int scheduleOptions = 0;

        if (request.scheduledAt() != null) {
            scheduleOptions++;
        }

        if (request.delaySeconds() != null) {
            scheduleOptions++;
        }

        if (scheduleOptions == 0) {
            return null;
        }

        if (scheduleOptions > 1) {
            throw new ResponseStatusException(
                BAD_REQUEST,
                "Use only one schedule option: scheduledAt or delaySeconds."
            );
        }

        OffsetDateTime now = OffsetDateTime.now(serverZone);
        OffsetDateTime scheduledAt;

        if (request.scheduledAt() != null) {
            scheduledAt = request.scheduledAt();
        } else {
            scheduledAt = resolveDelay(
                now,
                Duration.ofSeconds(request.delaySeconds())
            );
        }

        if (!scheduledAt.isAfter(now)) {
            throw new ResponseStatusException(
                BAD_REQUEST,
                "Scheduled time must be in the future."
            );
        }

        return scheduledAt;
    }

    private OffsetDateTime resolveDelay(
        OffsetDateTime now,
        Duration delay
    ) {
        if (delay.isZero() || delay.isNegative()) {
            throw new ResponseStatusException(
                BAD_REQUEST,
                "Delay must be greater than zero."
            );
        }

        return now.plus(delay);
    }

    private void publishIrCommand(String command, String topic) {
        mqttGateway.publish(command, topic, 1);
    }
}
