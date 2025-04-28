package com.banreservas.product.mapper;

import com.banreservas.product.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProductEventMapper {

    ProductEventMapper INSTANCE = Mappers.getMapper(ProductEventMapper.class);

    com.banreservas.product.avro.v1.ProductCreatedEventV1 toEvent(Product product);

    Product toProduct(com.banreservas.product.avro.v1.ProductCreatedEventV1 event);
}
