package com.banreservas.product.unit.service.impl;

import com.banreservas.product.model.Product;
import com.banreservas.product.repository.ProductRepository;
import com.banreservas.product.service.ProductEventPublishService;
import com.banreservas.product.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static com.banreservas.product.util.JsonUtils.readFile;
import static com.banreservas.product.util.JsonUtils.toObject;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @InjectMocks
    private ProductServiceImpl productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductEventPublishService productEventPublishService;

    @Test
    void ProductService_CreateProduct_Success() {
        var product = toObject(readFile("/service/product-expected.json"), Product.class);
        var productWithOutId = toObject(readFile("/service/product.json"), Product.class);
        var monoProduct = Mono.just(product);

        when(productRepository.save(any(Product.class))).thenReturn(monoProduct);
        when(productEventPublishService.sendProductCreatedEvent(any(Product.class))).thenReturn(monoProduct);

        StepVerifier.create(productService.createProduct(productWithOutId))
                .expectNext(product)
                .verifyComplete();
    }

    @Test
    void ProductService_UpdateProduct_Success() {
        var product = Product.builder()
                .id("68110d3751d1f7328efa0ece")
                .name("Name")
                .description("Description")
                .sku("324454")
                .category("Monitor")
                .price(3)
                .build();
        var newProduct = toObject(readFile("/service/product-expected.json"), Product.class);
        var monoProduct = Mono.just(newProduct);


        when(productRepository.save(any(Product.class))).thenReturn(monoProduct);
        when(productEventPublishService.sendProductUpdatedEvent(any(Product.class))).thenReturn(monoProduct);

        StepVerifier.create(productService.updateProduct(product, newProduct))
                .expectNext(newProduct)
                .verifyComplete();
    }

    @Test
    void ProductService_GetOne_Success() {
        var productId = "68110d3751d1f7328efa0ece";
        var product = toObject(readFile("/service/product-expected.json"), Product.class);
        var monoProduct = Mono.just(product);

        when(productRepository.findById(anyString())).thenReturn(monoProduct);

        StepVerifier.create(productService.getOne(productId))
                .expectNext(product)
                .verifyComplete();
    }

    @Test
    void ProductService_ListByCategory_Success() {
        var product = toObject(readFile("/service/product-expected.json"), Product.class);
        var fluxProduct = Flux.just(product);

        when(productRepository.findAllByCategory(anyString())).thenReturn(fluxProduct);

        var category = "Monitor";
        StepVerifier.create(productService.listByCategory(category))
                .expectNextMatches(product::equals)
                .verifyComplete();
    }

    @Test
    void ProductService_GetAll_Success() {
        var product = toObject(readFile("/service/product-expected.json"), Product.class);
        var fluxProduct = Flux.just(product);

        when(productRepository.findAll()).thenReturn(fluxProduct);

        StepVerifier.create(productService.getAll())
                .expectNextMatches(product::equals)
                .verifyComplete();
    }

    @Test
    void ProductService_Delete_Success() {
        var product = toObject(readFile("/service/product-expected.json"), Product.class);

        when(productRepository.delete(any(Product.class))).thenReturn(Mono.empty());
        when(productEventPublishService.sendProductDeletedEvent(any(Product.class))).thenReturn(Mono.empty());

        StepVerifier.create(productService.deleteProduct(product))
                .verifyComplete();

    }
}