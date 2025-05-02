package com.banreservas.product.service.impl;

import com.banreservas.product.service.ExchangeRateCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExchangeRateCacheServiceImpl implements ExchangeRateCacheService {

    private final ReactiveRedisTemplate<String, String> redisTemplate;

    private static final String PREFIX = "exchange-rate";

    private String buildKey(String fromCurrency, String toCurrency) {
        return String.format("%s:%s-%s", PREFIX, fromCurrency.toUpperCase(), toCurrency.toUpperCase());
    }

    @Override
    public Mono<Double> getExchangeRate(String fromCurrency, String toCurrency) {
        String key = buildKey(fromCurrency, toCurrency);
        return redisTemplate.opsForValue()
                .get(key)
                .doOnNext(value -> log.trace("Cache HIT for {}: {}", key, value))
                .map(Double::parseDouble)
                .switchIfEmpty(Mono.defer(() -> {
                    log.debug("Cache MISS for {}", key);
                    return Mono.empty();
                }));
    }

    @Override
    public Mono<Void> putExchangeRate(String fromCurrency, String toCurrency, Double rate) {
        String key = buildKey(fromCurrency, toCurrency);
        return redisTemplate.opsForValue()
                .set(key, String.valueOf(rate))
                .doOnNext(success -> {
                    if (success) {
                        log.debug("Cached exchange rate for {}: {}", key, rate);
                    } else {
                        log.warn("Failed to cache exchange rate for {}", key);
                    }
                })
                .then();
    }
}
