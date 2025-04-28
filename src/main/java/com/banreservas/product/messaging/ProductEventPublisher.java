package com.banreservas.product.messaging;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ProductEventPublisher<T> {

    private final KafkaTemplate<String, T> kafkaTemplate;

    @Value("${spring.kafka.topic}")
    private String topic;

    public Mono<SendResult<String, T>> send(String key, T event) {

        return Mono.fromFuture(kafkaTemplate.send(topic, key, event))
                .doOnError(Mono::error);
    }

}
