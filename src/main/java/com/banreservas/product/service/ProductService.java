package com.banreservas.product.service;

import com.banreservas.product.model.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductService {

    Mono<Product> createProduct(Product product);

    Flux<Product> getAll();

    Mono<Product> getOne(String id);

}
