package com.agileengine.dto;

import com.agileengine.model.Product;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class ProductIdDtoMapper implements Function<Product, ProductIdDto> {
    @Override
    public ProductIdDto apply(Product product) {
        return new ProductIdDto(product.getId());
    }
}
