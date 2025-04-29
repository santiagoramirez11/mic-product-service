package com.banreservas.product.config;

import com.banreservas.product.avro.v1.ProductCreatedEventV1;
import com.banreservas.product.avro.v1.ProductUpdatedEventV1;
import com.banreservas.product.messaging.ProductEventPublisher;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
@ConfigurationPropertiesScan
public class KafkaEventPublisherConfig {

    @Bean
    public ProductEventPublisher<ProductCreatedEventV1> productCreatedEventPublisher(
            KafkaTemplate<String, ProductCreatedEventV1> kafkaTemplate, KafkaBindingsProperties kafkaBindingsProperties) {

        var topic = kafkaBindingsProperties.getTopics().get(ProductCreatedEventV1.class.getCanonicalName());
        return new ProductEventPublisher<>(kafkaTemplate, topic);
    }

    @Bean
    public ProductEventPublisher<ProductUpdatedEventV1> productUpdatedEventPublisher(
            KafkaTemplate<String, ProductUpdatedEventV1> kafkaTemplate, KafkaBindingsProperties kafkaBindingsProperties) {

        var topic = kafkaBindingsProperties.getTopics().get(ProductUpdatedEventV1.class.getCanonicalName());
        return new ProductEventPublisher<>(kafkaTemplate, topic);
    }
}
