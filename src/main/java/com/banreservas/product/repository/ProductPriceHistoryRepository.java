package com.banreservas.product.repository;

import com.banreservas.product.model.ProductPriceHistory;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface ProductPriceHistoryRepository extends ReactiveMongoRepository<ProductPriceHistory, String> {

    Flux<ProductPriceHistory> findByProductId(String productId);
}
