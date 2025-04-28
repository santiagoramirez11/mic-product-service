package com.banreservas.product.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class Product {

    private String id;

    private String name;

    private String description;

    private double price;

    private String sku;

    private String category;

}
