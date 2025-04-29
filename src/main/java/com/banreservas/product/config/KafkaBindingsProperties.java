package com.banreservas.product.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.bindings")
public class KafkaBindingsProperties {

    private Map<String, String> topics;

}
