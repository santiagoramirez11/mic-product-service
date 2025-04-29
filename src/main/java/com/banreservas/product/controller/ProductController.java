package com.banreservas.product.controller;

import com.banreservas.openapi.models.ProductRequestDto;
import com.banreservas.openapi.models.ProductResponseDto;
import com.banreservas.product.constant.Endpoint;
import com.banreservas.product.mapper.ProductDtoMapper;
import com.banreservas.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(Endpoint.API_BASE)
public class ProductController implements com.banreservas.openapi.controllers.ProductsApi {

    private final ProductService productService;

    @Override
    public Mono<ResponseEntity<ProductResponseDto>> createProduct(Mono<ProductRequestDto> productRequestDto, ServerWebExchange exchange) {
        return productRequestDto
                .map(ProductDtoMapper.INSTANCE::toProduct)
                .doOnNext(info -> log.trace("Receive Product [{}]", info))
                .flatMap(productService::createProduct)
                .doOnSuccess(product -> log.info("Success creating product [id: {}]", product.getId()))
                .map(ProductDtoMapper.INSTANCE::toProductDto)
                .map(ResponseEntity.status(HttpStatus.CREATED)::body);
    }
}
