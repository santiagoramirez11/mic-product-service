package com.banreservas.product.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@Document(collection = "product_price_history")
public class ProductPriceHistory {

    @Id
    private String id;

    private String productId;

    private double originalPrice;

    private double newPrice;

    private String originalCurrency;

    private String newCurrency;

    private Instant changeDate;

}
