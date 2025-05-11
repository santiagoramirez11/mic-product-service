package com.banreservas.product.unit.service.impl;

import com.banreservas.product.avro.v1.ProductCreatedEventV1;
import com.banreservas.product.avro.v1.ProductDeletedEventV1;
import com.banreservas.product.avro.v1.ProductUpdatedEventV1;
import com.banreservas.product.messaging.ProductEventPublisher;
import com.banreservas.product.model.Product;
import com.banreservas.product.service.impl.ProductEventPublishServiceImpl;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.kafka.support.SendResult;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static com.banreservas.product.util.JsonUtils.readFile;
import static com.banreservas.product.util.JsonUtils.toObject;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

class ProductEventPublishServiceImplTest {

    private final ProductEventPublishServiceImpl productEventPublishService;

    private final ProductEventPublisher<ProductCreatedEventV1> productCreatedEventPublisher;

    private final ProductEventPublisher<ProductUpdatedEventV1> productUpdatedEventPublisher;

    private final ProductEventPublisher<ProductDeletedEventV1> productDeletedEventPublisher;


    @SuppressWarnings("unchecked")
    public ProductEventPublishServiceImplTest() {
        this.productCreatedEventPublisher = Mockito.mock(ProductEventPublisher.class);
        this.productUpdatedEventPublisher = Mockito.mock(ProductEventPublisher.class);
        this.productDeletedEventPublisher = Mockito.mock(ProductEventPublisher.class);
        this.productEventPublishService = new ProductEventPublishServiceImpl(
                productCreatedEventPublisher,
                productUpdatedEventPublisher,
                productDeletedEventPublisher);
    }

    @Test
    void ProductEventPublishService_sendProductCreatedEvent_Success() {
        var productCreatedEvent = toObject(readFile("/service/product-expected.json"), ProductCreatedEventV1.class);
        var product = toObject(readFile("/service/product-expected.json"), Product.class);

        final var producerRecord = new ProducerRecord<>("createtopic", productCreatedEvent.getId(), productCreatedEvent);
        final var sendResult = new SendResult<>(producerRecord, null);


        Mockito.when(productCreatedEventPublisher.send(anyString(), any(ProductCreatedEventV1.class))).thenReturn(Mono.just(sendResult));

        StepVerifier.create(productEventPublishService.sendProductCreatedEvent(product))
                .expectNext(product)
                .verifyComplete();
    }

    @Test
    void ProductEventPublishService_sendProductUpdatedEvent_Success() {
        var productUpdatedEventV1 = toObject(readFile("/service/product-expected.json"), ProductUpdatedEventV1.class);
        var product = toObject(readFile("/service/product-expected.json"), Product.class);

        final var producerRecord = new ProducerRecord<>("update", productUpdatedEventV1.getId(), productUpdatedEventV1);
        final var sendResult = new SendResult<>(producerRecord, null);


        Mockito.when(productUpdatedEventPublisher.send(anyString(), any(ProductUpdatedEventV1.class))).thenReturn(Mono.just(sendResult));

        StepVerifier.create(productEventPublishService.sendProductUpdatedEvent(product))
                .expectNext(product)
                .verifyComplete();
    }

    @Test
    void ProductEventPublishService_sendProductDeletedEvent_Success() {
        var product = toObject(readFile("/service/product-expected.json"), Product.class);
        var productDeletedEventV1 = ProductDeletedEventV1.newBuilder()
                .setId(product.getId())
                .build();

        final var producerRecord = new ProducerRecord<>("deleteTopic", productDeletedEventV1.getId(), productDeletedEventV1);
        final var sendResult = new SendResult<>(producerRecord, null);

        Mockito.when(productDeletedEventPublisher.send(anyString(), any(ProductDeletedEventV1.class)))
                .thenReturn(Mono.just(sendResult));

        StepVerifier.create(productEventPublishService.sendProductDeletedEvent(product))
                .verifyComplete();

        Mockito.verify(productDeletedEventPublisher).send(product.getId(), productDeletedEventV1);
    }
}