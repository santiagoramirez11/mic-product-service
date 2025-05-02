package com.banreservas.product.service.impl;

import com.banreservas.product.integration.ExchangeRateApiClient;
import com.banreservas.product.model.Product;
import com.banreservas.product.model.ProductPriceHistory;
import com.banreservas.product.service.ExchangeRateCacheService;
import com.banreservas.product.service.ProductCurrencyChangeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class ProductCurrencyChangeServiceImpl implements ProductCurrencyChangeService {

    @Value("${app.service.default-currency}")
    private String systemCurrency;

    private final ExchangeRateCacheService exchangeRateCacheService;
    private final ExchangeRateApiClient exchangeRateApiClient;

    @Override
    public Function<String, Mono<ProductPriceHistory>> convert(Product product) {
        return newCurrency -> exchangeRateCacheService.getExchangeRate(systemCurrency, newCurrency)
                .switchIfEmpty(getRateAndCache(systemCurrency, newCurrency))
                .map(rate -> ProductPriceHistory.builder()
                        .productId(product.getId())
                        .originalCurrency(systemCurrency)
                        .originalPrice(product.getPrice())
                        .newPrice(product.getPrice()*rate)
                        .newCurrency(newCurrency)
                        .changeDate(Instant.now())
                        .build());
    }

    private Mono<Double> getRateAndCache(String systemCurrency, String newCurrency) {
        return exchangeRateApiClient.getConversionRate(systemCurrency, newCurrency)
                .flatMap(rate -> exchangeRateCacheService.putExchangeRate(systemCurrency, newCurrency, rate)
                        .thenReturn(rate));
    }
}
