package com.sondm.iot;

import com.sondm.iot.config.MqttProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(MqttProperties.class)
public class IotApplication {

	public static void main(String[] args) {
		SpringApplication.run(IotApplication.class, args);
	}

}
