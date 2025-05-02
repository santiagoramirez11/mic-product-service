package com.banreservas.product.unit.service.impl;

import com.banreservas.product.model.Product;
import com.banreservas.product.model.ProductPriceHistory;
import com.banreservas.product.repository.ProductPriceHistoryRepository;
import com.banreservas.product.service.ProductCurrencyChangeService;
import com.banreservas.product.service.impl.ProductPriceServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static com.banreservas.product.util.JsonUtils.readFile;
import static com.banreservas.product.util.JsonUtils.toObject;

@ExtendWith(MockitoExtension.class)
class ProductPriceServiceImplTest {

    @InjectMocks
    ProductPriceServiceImpl productPriceService;

    @Mock
    ProductCurrencyChangeService productCurrencyChangeService;

    @Mock
    ProductPriceHistoryRepository productPriceHistoryRepository;

    @Test
    void ProductPriceServiceImpl_getProductWithCurrencyChanged_ShouldReturnProductWithUpdatedPrice() {

        var product = toObject(readFile("/service/product-expected.json"), Product.class);
        var productPriceHistory = toObject(readFile("/service/product-price-history.json"), ProductPriceHistory.class);

        ReflectionTestUtils.setField(productPriceService, "systemCurrency", "DOP");

        Mockito.when(productCurrencyChangeService.convert(ArgumentMatchers.any(Product.class)))
                .thenReturn(returnString -> Mono.just(productPriceHistory));

        Mockito.when(productPriceHistoryRepository.save(ArgumentMatchers.any(ProductPriceHistory.class)))
                .thenReturn(Mono.just(productPriceHistory));

        StepVerifier.create(productPriceService.getProductWithCurrencyChanged(product, "USD"))
                .expectNextMatches(updatedProduct -> {
                    assert updatedProduct.getPrice() == productPriceHistory.getNewPrice();
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void getProductWithCurrencyChanged_ShouldHandleNullCurrency() {
        var product = Product.builder()
                .id("1")
                .name("Laptop")
                .price(100.0)
                .build();

        StepVerifier.create(productPriceService.getProductWithCurrencyChanged(product, null))
                .expectNextMatches(updatedProduct ->
                        updatedProduct.getPrice() == 100.0)
                .verifyComplete();
    }

}