package com.banreservas.product.mapper;

import com.banreservas.openapi.models.ProductRequestDto;
import com.banreservas.openapi.models.ProductResponseDto;
import com.banreservas.product.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProductDtoMapper {

    ProductDtoMapper INSTANCE = Mappers.getMapper(ProductDtoMapper.class);

    ProductResponseDto toProductDto(Product product);

    Product toProduct(ProductRequestDto productDto);

}
