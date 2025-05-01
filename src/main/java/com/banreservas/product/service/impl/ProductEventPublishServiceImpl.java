package com.banreservas.product.service.impl;

import com.banreservas.product.avro.v1.ProductCreatedEventV1;
import com.banreservas.product.avro.v1.ProductDeletedEventV1;
import com.banreservas.product.avro.v1.ProductUpdatedEventV1;
import com.banreservas.product.messaging.ProductEventPublisher;
import com.banreservas.product.model.Product;
import com.banreservas.product.service.ProductEventPublishService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static com.banreservas.product.mapper.ProductEventMapper.PRODUCT_EVENT_MAPPER;

@Service
@RequiredArgsConstructor
public class ProductEventPublishServiceImpl implements ProductEventPublishService {

    private final ProductEventPublisher<ProductCreatedEventV1> productCreatedEventPublisher;

    private final ProductEventPublisher<ProductUpdatedEventV1> productUpdatedEventPublisher;

    private final ProductEventPublisher<ProductDeletedEventV1> productDeletedEventPublisher;

    @Override
    public Mono<Product> sendProductCreatedEvent(Product product) {

        return Mono.just(product)
                .map(PRODUCT_EVENT_MAPPER::toCreatedEvent)
                .flatMap(event -> productCreatedEventPublisher.send(product.getId(), event))
                .map(SendResult::getProducerRecord)
                .map(producerRecord -> PRODUCT_EVENT_MAPPER.toProduct(producerRecord.value()));
    }

    @Override
    public Mono<Product> sendProductUpdatedEvent(Product product) {
        return Mono.just(product)
                .map(PRODUCT_EVENT_MAPPER::toUpdatedEvent)
                .flatMap(event -> productUpdatedEventPublisher.send(product.getId(), event))
                .map(SendResult::getProducerRecord)
                .map(producerRecord -> PRODUCT_EVENT_MAPPER.toProduct(producerRecord.value()));
    }

    @Override
    public Mono<Void> sendProductDeletedEvent(Product product) {
        return productDeletedEventPublisher.send(product.getId(), ProductDeletedEventV1.newBuilder()
                        .setId(product.getId())
                        .build())
                .then();
    }


}
