package com.banreservas.product.service;

import reactor.core.publisher.Mono;

public interface ExchangeRateCacheService {

    Mono<Double> getExchangeRate(String fromCurrency, String toCurrency);

    Mono<Void> putExchangeRate(String fromCurrency, String toCurrency, Double rate);
}
