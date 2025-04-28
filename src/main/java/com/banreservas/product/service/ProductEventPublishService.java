package com.banreservas.product.service;

import com.banreservas.product.model.Product;
import reactor.core.publisher.Mono;

public interface ProductEventPublishService {

    Mono<Product> sendProductCreatedEvent(Product product);
}
