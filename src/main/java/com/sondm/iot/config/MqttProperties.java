package com.sondm.iot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;

@ConfigurationProperties(prefix = "mqtt")
public record MqttProperties (
    String brokerUrl,
    String clientId
) {

}
