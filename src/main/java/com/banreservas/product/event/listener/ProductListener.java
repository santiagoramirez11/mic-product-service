package com.banreservas.product.event.listener;

import com.banreservas.product.avro.v1.ProductCreatedEventV1;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProductListener {

    @KafkaListener(topics = "${spring.kafka.topic}")
    public void consumer(ConsumerRecord<String, ProductCreatedEventV1> consumerRecord){
        log.info("Dato recibido [{}]",consumerRecord.value());
    }
}
