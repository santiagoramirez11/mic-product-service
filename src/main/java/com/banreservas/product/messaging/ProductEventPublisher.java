package com.banreservas.product.messaging;


import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ProductEventPublisher<T> {

    private final KafkaTemplate<String, T> kafkaTemplate;

    private final String topic;

    public Mono<SendResult<String, T>> send(String key, T event) {

        return Mono.fromFuture(kafkaTemplate.send(topic, key, event))
                .doOnError(Mono::error);
    }

}
