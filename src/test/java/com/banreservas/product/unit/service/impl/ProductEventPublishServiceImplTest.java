package com.banreservas.product.unit.service.impl;

import com.banreservas.product.avro.v1.ProductCreatedEventV1;
import com.banreservas.product.avro.v1.ProductDeletedEventV1;
import com.banreservas.product.avro.v1.ProductUpdatedEventV1;
import com.banreservas.product.messaging.ProductEventPublisher;
import com.banreservas.product.model.Product;
import com.banreservas.product.service.impl.ProductEventPublishServiceImpl;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.SendResult;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static com.banreservas.product.util.JsonUtils.readFile;
import static com.banreservas.product.util.JsonUtils.toObject;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
class ProductEventPublishServiceImplTest {

    @InjectMocks
    private ProductEventPublishServiceImpl productEventPublishService;

    @Mock
    private ProductEventPublisher<ProductCreatedEventV1> productCreatedEventPublisher;

    @Mock
    private ProductEventPublisher<ProductUpdatedEventV1> productUpdatedEventPublisher;

    @Mock
    private ProductEventPublisher<ProductDeletedEventV1> productDeletedEventPublisher;

    @Test
    void ProductEventPublishService_sendProductCreatedEvent_Success() {
        var productCreatedEvent = toObject(readFile("/service/product-expected.json"), ProductCreatedEventV1.class);
        var product = toObject(readFile("/service/product-expected.json"), Product.class);

        final var producerRecord = new ProducerRecord<>("createtopic", productCreatedEvent.getId(), productCreatedEvent);
        final var sendResult = new SendResult<>(producerRecord, null);


        Mockito.when(productCreatedEventPublisher.send(anyString(), any(ProductCreatedEventV1.class))).thenReturn(Mono.just(sendResult));

        //TODO Mejorar el test
        StepVerifier.create(productEventPublishService.sendProductCreatedEvent(product))
                .thenAwait(Duration.ofSeconds(4))
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

        //TODO Mejorar el test
        StepVerifier.create(productEventPublishService.sendProductUpdatedEvent(product))
                .thenAwait(Duration.ofSeconds(4))
                .expectNext(product)
                .verifyComplete();
    }

    @Test
    void ProductEventPublishService_sendProductDeletedEvent_Success() {

    }

}