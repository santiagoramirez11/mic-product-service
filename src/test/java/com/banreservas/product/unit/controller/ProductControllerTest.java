package com.banreservas.product.unit.controller;

import com.banreservas.product.controller.ProductController;
import com.banreservas.product.exception.ProductNotFoundException;
import com.banreservas.product.model.Product;
import com.banreservas.product.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static com.banreservas.product.util.JsonUtils.readFile;
import static com.banreservas.product.util.JsonUtils.toObject;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @InjectMocks
    private ProductController productController;

    @Mock
    private ProductService productService;

    @Mock
    private ServerWebExchange serverWebExchange;


    @Test
    void ProductController_CreateProduct_ShouldReturnsProduct() {
        var productRequestDto = toObject(readFile("/controller/product.json"), com.banreservas.openapi.models.ProductRequestDto.class);
        var product = toObject(readFile("/controller/product-expected.json"), Product.class);
        var expectedResponse = toObject(readFile("/controller/product-expected.json"), com.banreservas.openapi.models.ProductResponseDto.class);


        when(productService.createProduct(any(Product.class))).thenReturn(Mono.just(product));

        StepVerifier.create(productController.createProduct(productRequestDto, serverWebExchange))
                .expectNext(ResponseEntity.status(HttpStatus.CREATED).body(expectedResponse))
                .verifyComplete();
    }

    @Test
    void ProductController_UpdateProduct_ShouldReturnsProduct() {
        var productRequestDto = toObject(readFile("/controller/product.json"), com.banreservas.openapi.models.ProductRequestDto.class);
        var product = toObject(readFile("/controller/product-expected.json"), Product.class);
        var expectedResponse = toObject(readFile("/controller/product-expected.json"), com.banreservas.openapi.models.ProductResponseDto.class);

        var productId = "68110d3751d1f7328efa0ece";

        when(productService.getOne(anyString())).thenReturn(Mono.just(product));
        when(productService.updateProduct(any(Product.class), any(Product.class))).thenReturn(Mono.just(product));

        StepVerifier.create(productController.updateProduct(productId, productRequestDto, serverWebExchange))
                .expectNext(ResponseEntity.ok(expectedResponse))
                .verifyComplete();
    }

    @Test
    void ProductController_UpdateProduct_ShouldThrowProductNotFoundException_WhenProductNotFound() {
        var productRequestDto = toObject(readFile("/controller/product.json"), com.banreservas.openapi.models.ProductRequestDto.class);
        var productId = "68110d3751d1f7328efa0ece";

        when(productService.getOne(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(productController.updateProduct(productId, productRequestDto, serverWebExchange))
                .verifyError(ProductNotFoundException.class);
    }
}