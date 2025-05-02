package com.banreservas.product.unit.service.impl;

import com.banreservas.product.integration.ExchangeRateApiClient;
import com.banreservas.product.model.Product;
import com.banreservas.product.service.ExchangeRateCacheService;
import com.banreservas.product.service.impl.ProductCurrencyChangeServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static com.banreservas.product.util.JsonUtils.readFile;
import static com.banreservas.product.util.JsonUtils.toObject;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductCurrencyChangeServiceImplTest {

    @InjectMocks
    private ProductCurrencyChangeServiceImpl productCurrencyChangeService;

    @Mock
    private ExchangeRateCacheService exchangeRateCacheService;

    @Mock
    private ExchangeRateApiClient exchangeRateApiClient;

    @Test
    void convert_ShouldReturnConvertedProduct() {

        var product = toObject(readFile("/service/product-expected.json"), Product.class);

        ReflectionTestUtils.setField(productCurrencyChangeService, "systemCurrency", "DOP");
        when(exchangeRateCacheService.getExchangeRate(anyString(), anyString()))
                .thenReturn(Mono.just(0.13));
        when(exchangeRateApiClient.getConversionRate(anyString(), anyString()))
                .thenReturn(Mono.just(0.13));
        StepVerifier.create(productCurrencyChangeService.convert(product).apply("USD"))
                .expectNextMatches(updatedProduct ->
                        updatedProduct.getOriginalPrice() == product.getPrice() &&
                                updatedProduct.getNewPrice() == product.getPrice() * 0.13)
                .verifyComplete();
    }
}