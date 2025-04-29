package com.banreservas.product.mapper;

import com.banreservas.product.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProductEventMapper {

    ProductEventMapper PRODUCT_EVENT_MAPPER = Mappers.getMapper(ProductEventMapper.class);

    com.banreservas.product.avro.v1.ProductCreatedEventV1 toCreatedEvent(Product product);

    Product toProduct(com.banreservas.product.avro.v1.ProductCreatedEventV1 event);

    com.banreservas.product.avro.v1.ProductUpdatedEventV1 toUpdatedEvent(Product product);

    Product toProduct(com.banreservas.product.avro.v1.ProductUpdatedEventV1 event);
}
