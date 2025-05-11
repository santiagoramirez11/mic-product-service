package com.banreservas.product.controller;

import com.banreservas.openapi.controllers.ProductsApi;
import com.banreservas.openapi.models.ProductPriceHistoryItemDto;
import com.banreservas.openapi.models.ProductRequestDto;
import com.banreservas.openapi.models.ProductResponseDto;
import com.banreservas.product.exception.ProductNotFoundException;
import com.banreservas.product.mapper.ProductPriceMapper;
import com.banreservas.product.service.ProductPriceService;
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
@RequestMapping("${app.api.base-url}")
public class ProductController implements ProductsApi {

    private final ProductService productService;

    private final ProductPriceService productPriceService;

    @Override
    public Mono<ResponseEntity<Flux<ProductResponseDto>>> listProducts(String currency, ServerWebExchange exchange) {
        throw new ProductNotFoundException("0");
    }

    @Override
    public Mono<ResponseEntity<Flux<ProductPriceHistoryItemDto>>> productPriceHistory(String productId, ServerWebExchange exchange) {
        Flux<ProductPriceHistoryItemDto> results = productService.getOne(productId)
                .switchIfEmpty(Mono.error(new ProductNotFoundException(productId)))
                .doOnNext(productPriceHistory -> log.trace("Getting product Price [{}]", productPriceHistory.getId()))
                .flatMapMany(productPriceService::getProductPriceHistory)
                .map(ProductPriceMapper.PRICE_MAPPER::toProductPriceItem);

        return Mono.just(results)
                .switchIfEmpty(Mono.error(new ProductNotFoundException(productId)))
                .map(ResponseEntity::ok);
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
        return productService.getOne(id)
                .doOnNext(product -> log.trace("Searching Product: [{}]", id))
                .switchIfEmpty(Mono.error(new ProductNotFoundException(id)))
                .flatMap(product -> productPriceService.getProductWithCurrencyChanged(product, currency))
                .doOnSuccess(response -> log.debug("Success searching Product: [{}:{}]", response.getId(), response.getName()))
                .map(MAPPER::toProductDto)
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<Flux<ProductResponseDto>>> getProductByCategory(String category, String currency, ServerWebExchange exchange) {
        Flux<ProductResponseDto> result = productService.listByCategory(category)
                .doOnNext(product -> log.trace("List Products by Category: [{}]", category))
                .flatMap(product -> productPriceService.getProductWithCurrencyChanged(product, currency))
                .map(MAPPER::toProductDto);
        return Mono.just(ResponseEntity.ok(result));
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
