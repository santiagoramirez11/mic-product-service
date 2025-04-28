package com.banreservas.product.controller;

import com.banreservas.product.dto.ProductDto;
import com.banreservas.product.mapper.ProductDtoMapper;
import com.banreservas.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public Mono<ResponseEntity<ProductDto>> createProduct(@RequestBody Mono<ProductDto> productMono){

        return productMono
                .map(ProductDtoMapper.INSTANCE::toProduct)
                .doOnNext(info -> log.trace("Receive Product [{}]", info))
                .flatMap(productService::createProduct)
                .map(ProductDtoMapper.INSTANCE::toProductDto)
                .map(ResponseEntity.status(HttpStatus.CREATED)::body);
    }
}
