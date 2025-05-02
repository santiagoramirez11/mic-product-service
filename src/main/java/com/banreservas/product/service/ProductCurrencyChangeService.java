package com.banreservas.product.service;

import com.banreservas.product.model.Product;
import com.banreservas.product.model.ProductPriceHistory;
import reactor.core.publisher.Mono;

import java.util.function.Function;

public interface ProductCurrencyChangeService {

    Function<String, Mono<ProductPriceHistory>> convert(Product product);
}
