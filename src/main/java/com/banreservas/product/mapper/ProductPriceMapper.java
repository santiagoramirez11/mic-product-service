package com.banreservas.product.mapper;

import com.banreservas.openapi.models.ProductPriceHistoryItemDto;
import com.banreservas.product.model.ProductPriceHistory;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProductPriceMapper {
    
    ProductPriceMapper PRICE_MAPPER = Mappers.getMapper(ProductPriceMapper.class);

    ProductPriceHistoryItemDto toProductPriceItem(ProductPriceHistory productPriceHistory);
}
