package com.banreservas.product.mapper;

import com.banreservas.product.dto.ProductDto;
import com.banreservas.product.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProductDtoMapper {

    ProductDtoMapper INSTANCE = Mappers.getMapper(ProductDtoMapper.class);

    ProductDto toProductDto(Product product);

    Product toProduct(ProductDto productDto);

}
