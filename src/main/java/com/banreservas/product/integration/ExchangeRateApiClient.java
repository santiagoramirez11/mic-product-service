package com.banreservas.product.integration;

import com.banreservas.product.integration.dto.ExchangeRateResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ExchangeRateApiClient {

    @Value("${external.exchange-rate-api.key}")
    private String apiKey;

    @Value("${external.exchange-rate-api.base-url}")
    private String baseUrl;

    private final WebClient webClient;

    public Mono<Double> getConversionRate(String baseCurrency, String targetCurrency) {
        String url = String.format("%s/v6/%s/pair/%s/%s", baseUrl, apiKey, baseCurrency, targetCurrency);

        return webClient
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(ExchangeRateResponseDto.class)
                .filter(response -> "success".equalsIgnoreCase(response.getResult()))
                .map(ExchangeRateResponseDto::getConversion_rate);
    }
}
