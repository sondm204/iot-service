package com.sondm.iot.config;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

@Configuration
@EnableIntegration
@IntegrationComponentScan
public class MqttConfig {

    private static final Logger log =
        LoggerFactory.getLogger(MqttConfig.class);

    private static final String LIGHT_STATUS_TOPIC =
        "devices/esp32-01/status/light";

    @Bean
    public MqttPahoClientFactory mqttClientFactory(
        MqttProperties properties
    ) {
        MqttConnectOptions options = new MqttConnectOptions();

        options.setServerURIs(
            new String[]{properties.brokerUrl()}
        );

        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setConnectionTimeout(10);

        DefaultMqttPahoClientFactory factory =
            new DefaultMqttPahoClientFactory();

        factory.setConnectionOptions(options);

        return factory;
    }

    @Bean
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel mqttInboundChannel() {
        return new DirectChannel();
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    public MessageHandler mqttOutboundHandler(
        MqttPahoClientFactory clientFactory,
        MqttProperties properties
    ) {
        MqttPahoMessageHandler handler =
            new MqttPahoMessageHandler(
                properties.clientId(),
                clientFactory
            );

        handler.setAsync(true);
        handler.setDefaultQos(1);
        handler.setDefaultRetained(false);

        return handler;
    }

    @Bean
    public MqttPahoMessageDrivenChannelAdapter mqttLightStatusInboundAdapter(
        MqttPahoClientFactory clientFactory,
        MqttProperties properties
    ) {
        MqttPahoMessageDrivenChannelAdapter adapter =
            new MqttPahoMessageDrivenChannelAdapter(
                properties.clientId() + "-light-status",
                clientFactory,
                LIGHT_STATUS_TOPIC
            );

        adapter.setQos(1);
        adapter.setOutputChannel(mqttInboundChannel());

        return adapter;
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttInboundChannel")
    public MessageHandler mqttLightStatusHandler() {
        return message -> {
            String topic = String.valueOf(
                message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC)
            );
            String lightState = String.valueOf(message.getPayload());

            log.info(
                "Received light status from topic [{}]: {}",
                topic,
                lightState
            );
        };
    }
}
