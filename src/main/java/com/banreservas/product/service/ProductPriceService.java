package com.banreservas.product.service;

import com.banreservas.product.model.Product;
import com.banreservas.product.model.ProductPriceHistory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductPriceService {

    Mono<Product> getProductWithCurrencyChanged(Product product, String targetCurrencyCode);

    Flux<ProductPriceHistory> getProductPriceHistory(Product product);
}
