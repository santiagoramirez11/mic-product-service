package com.banreservas.product.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class Product {

    private String id;

    @NotNull(message = "Product_name can not be null")
    @NotEmpty(message = "Product_name can not be empty")
    private String name;

    @NotNull(message = "Product_description can not be null")
    @NotEmpty(message = "Product_description can not be empty")
    private String description;

    @Min(0)
    private double price;

    @NotNull(message = "Product_sku can not be null")
    @NotEmpty(message = "Product_sku can not be empty")
    @Indexed(unique = true)
    private String sku;

    @NotNull(message = "Product_category can not be null")
    @NotEmpty(message = "Product_category can not be empty")
    private String category;

}
