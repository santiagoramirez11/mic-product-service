package com.banreservas.product.service.impl;

import com.banreservas.product.model.Product;
import com.banreservas.product.model.ProductPriceHistory;
import com.banreservas.product.repository.ProductPriceHistoryRepository;
import com.banreservas.product.service.ProductCurrencyChangeService;
import com.banreservas.product.service.ProductPriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class ProductPriceServiceImpl implements ProductPriceService {

    private final ProductCurrencyChangeService productCurrencyChangeService;

    private final ProductPriceHistoryRepository productPriceHistoryRepository;

    @Value("${app.service.default-currency}")
    private String systemCurrency;

    @Override
    public Mono<Product> getProductWithCurrencyChanged(Product product, String targetCurrencyCode) {
        if (targetCurrencyCode == null || systemCurrency.equals(targetCurrencyCode)) {
            return Mono.just(product);
        }

        return Mono.just(targetCurrencyCode)
                .flatMap(productCurrencyChangeService.convert(product))
                .flatMap(productPriceHistoryRepository::save)
                .map(updateProductWithCurrency(product));
    }

    @Override
    public Flux<ProductPriceHistory> getProductPriceHistory(Product product) {
        return productPriceHistoryRepository.findByProductId(product.getId());
    }

    private Function<ProductPriceHistory, Product> updateProductWithCurrency(Product product) {
        return productPriceHistory -> Product.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(productPriceHistory.getNewPrice())
                .sku(product.getSku())
                .category(product.getCategory())
                .build();
    }
}
