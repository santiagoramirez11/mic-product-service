package com.banreservas.product.service.impl;

import com.banreservas.product.mapper.ProductEventMapper;
import com.banreservas.product.messaging.ProductEventPublisher;
import com.banreservas.product.model.Product;
import com.banreservas.product.service.ProductEventPublishService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import com.banreservas.product.avro.v1.ProductCreatedEventV1;

@Service
@RequiredArgsConstructor
public class ProductEventPublishServiceImpl implements ProductEventPublishService {

    private final ProductEventPublisher<ProductCreatedEventV1> productEventPublisher;

    @Override
    public Mono<Product> sendProductCreatedEvent(Product product) {

        return Mono.just(product)
                .map(ProductEventMapper.INSTANCE::toEvent)
                .flatMap(event -> productEventPublisher.send(product.getId(), event))
                .map(SendResult::getProducerRecord)
                .map(ProducerRecord::value)
                .map(ProductEventMapper.INSTANCE::toProduct);
    }

}
