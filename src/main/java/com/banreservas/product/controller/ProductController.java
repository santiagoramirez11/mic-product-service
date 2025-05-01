package com.banreservas.product.controller;

import com.banreservas.openapi.controllers.ProductsApi;
import com.banreservas.openapi.models.ProductRequestDto;
import com.banreservas.openapi.models.ProductResponseDto;
import com.banreservas.product.constant.Endpoint;
import com.banreservas.product.exception.ProductNotFoundException;
import com.banreservas.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.banreservas.product.mapper.ProductDtoMapper.MAPPER;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(Endpoint.API_BASE)
public class ProductController implements ProductsApi {

    private final ProductService productService;

    @Override
    public Mono<ResponseEntity<Flux<ProductResponseDto>>> listProducts(String currency, ServerWebExchange exchange) {
        throw new ProductNotFoundException("0");
    }

    @Override
    public Mono<ResponseEntity<ProductResponseDto>> createProduct(@Valid ProductRequestDto productRequestDto, ServerWebExchange exchange) {
        return Mono.just(productRequestDto)
                .map(MAPPER::toProduct)
                .doOnNext(info -> log.trace("Receive Product [{}]", info))
                .flatMap(productService::createProduct)
                .doOnSuccess(product -> log.info("Success creating product [id: {}]", product.getId()))
                .map(MAPPER::toProductDto)
                .map(ResponseEntity.status(HttpStatus.CREATED)::body);
    }

    @Override
    public Mono<ResponseEntity<ProductResponseDto>> getProduct(String id, String currency, ServerWebExchange exchange) {
        throw new ProductNotFoundException(id);
    }

    @Override
    public Mono<ResponseEntity<ProductResponseDto>> updateProduct(String id, @Valid ProductRequestDto productRequestDto, ServerWebExchange exchange) {
        return productService.getOne(id)
                .switchIfEmpty(Mono.error(new ProductNotFoundException(id)))
                .doOnNext(product -> log.trace("Updating Product: [{}]", product))
                .flatMap(product -> productService.updateProduct(product, MAPPER.toProduct(productRequestDto)))
                .map(MAPPER::toProductDto)
                .doOnSuccess(product -> log.info("Success update product [id: {}]", product.getId()))
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteProduct(String id, ServerWebExchange exchange) {
        return productService.getOne(id)
                .switchIfEmpty(Mono.error(new ProductNotFoundException(id)))
                .doOnNext(product -> log.trace("Deleting Product: [ProductFound: {}]", product))
                .flatMap(productService::deleteProduct)
                .doOnSuccess(l -> log.info("Success delete product [id: {}]", id))
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}
