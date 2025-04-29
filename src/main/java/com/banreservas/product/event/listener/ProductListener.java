package com.banreservas.product.event.listener;

import com.banreservas.product.avro.v1.ProductCreatedEventV1;
import com.banreservas.product.avro.v1.ProductUpdatedEventV1;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProductListener {

    @KafkaListener(topics = "product-created-v1")
    public void consumerCreate(ConsumerRecord<String, ProductCreatedEventV1> consumerRecord) {
        log.info("Dato consumerCreate recibido [{}]", consumerRecord.value());
    }

    @KafkaListener(topics = "product-updated-v1")
    public void consumerUpdate(ConsumerRecord<String, ProductUpdatedEventV1> consumerRecord) {
        log.info("Dato consumerUpdate recibido [{}]", consumerRecord.value());
    }
}
