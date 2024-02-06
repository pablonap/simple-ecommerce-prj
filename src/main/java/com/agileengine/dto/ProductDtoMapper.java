package com.agileengine.dto;

import com.agileengine.model.Product;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class ProductDtoMapper implements Function<Product, ProductDto> {
    @Override
    public ProductDto apply(Product product) {
        return new ProductDto(product.getId(), product.getName(), product.getCode(), product.getDescription(), product.getPrice());
    }
}
