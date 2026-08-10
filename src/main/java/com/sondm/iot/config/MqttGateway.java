package com.sondm.iot.config;

import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.handler.annotation.Header;

@MessagingGateway(
    defaultRequestChannel = "mqttOutboundChannel"
)
public interface MqttGateway {

    void publish(
        String payload,
        @Header(MqttHeaders.TOPIC) String topic
    );

    void publish(
        String payload,
        @Header(MqttHeaders.TOPIC) String topic,
        @Header(MqttHeaders.QOS) int qos
    );
}
