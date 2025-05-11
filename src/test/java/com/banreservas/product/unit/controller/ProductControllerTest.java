package com.banreservas.product.unit.controller;

import com.banreservas.openapi.models.ProductPriceHistoryItemDto;
import com.banreservas.openapi.models.ProductResponseDto;
import com.banreservas.product.controller.ProductController;
import com.banreservas.product.exception.ProductNotFoundException;
import com.banreservas.product.model.Product;
import com.banreservas.product.model.ProductPriceHistory;
import com.banreservas.product.service.ProductPriceService;
import com.banreservas.product.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Objects;

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

    @Mock
    private ProductPriceService productPriceService;


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

    @Test
    void ProductController_DeleteProduct_ShouldReturnsNotContent() {
        var productId = "68110d3751d1f7328efa0ece";
        var product = toObject(readFile("/controller/product-expected.json"), Product.class);

        when(productService.getOne(anyString())).thenReturn(Mono.just(product));
        when(productService.deleteProduct(any(Product.class))).thenReturn(Mono.empty());

        StepVerifier.create(productController.deleteProduct(productId, serverWebExchange))
                .expectNext(ResponseEntity.noContent().build())
                .verifyComplete();
    }

    @Test
    void ProductController_DeleteProduct_ShouldThrowProductNotFoundException_WhenProductNotFound() {
        var productId = "68110d3751d1f7328efa0ece";

        when(productService.getOne(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(productController.deleteProduct(productId, serverWebExchange))
                .verifyError(ProductNotFoundException.class);
    }

    @Test
    void ProductController_GetProductByCategory_ShouldReturnsProduct() {
        var category = "electronics";
        var product = toObject(readFile("/controller/product-expected.json"), Product.class);
        var expectedResponse = toObject(readFile("/controller/product-expected.json"), ProductResponseDto.class);


        when(productService.listByCategory(anyString())).thenReturn(Flux.just(product));
        when(productPriceService.getProductWithCurrencyChanged(any(Product.class), anyString()))
                .thenReturn(Mono.just(product));

        StepVerifier.create(productController.getProductByCategory(category, "DOP", serverWebExchange))
                .expectNextMatches(response -> {
                    var body = response.getBody();
                    if (body == null || HttpStatus.OK != response.getStatusCode()) {
                        return false;
                    }

                    return Objects.requireNonNull(body.collectList().block()).contains(expectedResponse);
                })
                .verifyComplete();
    }

    @Test
    void ProductController_ProductPriceHistory_ShouldReturnsListOfPriceHistory() {
        var productId = "68110d3751d1f7328efa0ece";
        var product = toObject(readFile("/controller/product-expected.json"), Product.class);
        var productPriceHistory = toObject(readFile("/controller/product-price-expected.json"), ProductPriceHistory.class);
        var productPriceHistoryExpected = toObject(readFile("/controller/product-price-expected.json"), ProductPriceHistoryItemDto.class);

        when(productService.getOne(anyString())).thenReturn(Mono.just(product));
        when(productPriceService.getProductPriceHistory(any(Product.class)))
                .thenReturn(Flux.just(productPriceHistory));

        StepVerifier.create(productController.productPriceHistory(productId, serverWebExchange))
                .expectNextMatches(response -> {
                    var body = response.getBody();
                    if (body == null || HttpStatus.OK != response.getStatusCode()) {
                        return false;
                    }

                    return Objects.requireNonNull(body.collectList().block()).contains(productPriceHistoryExpected);
                })
                .verifyComplete();
    }

    @Test
    void ProductController_GetProduct_ShouldReturnsProduct() {
        var product = toObject(readFile("/controller/product-expected.json"), Product.class);
        var expectedResponse = toObject(readFile("/controller/product-expected.json"), com.banreservas.openapi.models.ProductResponseDto.class);

        var productId = "68110d3751d1f7328efa0ece";

        when(productService.getOne(anyString())).thenReturn(Mono.just(product));
        when(productPriceService.getProductWithCurrencyChanged(any(Product.class), anyString()))
                .thenReturn(Mono.just(product));

        StepVerifier.create(productController.getProduct(productId, "USD", serverWebExchange))
                .expectNext(ResponseEntity.ok(expectedResponse))
                .verifyComplete();
    }

    @Test
    void ProductController_GetProduct_ShouldThrowProductNotFoundException_WhenProductNotFound() {
        var productId = "68110d3751d1f7328efa0ece";

        when(productService.getOne(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(productController.getProduct(productId, null, serverWebExchange))
                .verifyError(ProductNotFoundException.class);
    }
}