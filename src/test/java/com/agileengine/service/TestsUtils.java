package com.agileengine.service;

import com.agileengine.dto.ProductCreateOrUpdateDto;
import com.agileengine.model.Product;

import java.math.BigDecimal;

public final class TestsUtils {
    private TestsUtils() {
    }

    public static ProductCreateOrUpdateDto productCreateOrUpdateDtoOf
        (String name,
         String code,
         String description,
         BigDecimal price) {
        ProductCreateOrUpdateDto dto = new ProductCreateOrUpdateDto(name, code, description, price);
        return dto;
    }

    public static Product productOf(Long id, String name, String code, String description, BigDecimal price) {
        final var product = new Product();
        product.setId(id);
        product.setName(name);
        product.setCode(code);
        product.setDescription(description);
        product.setPrice(price);

        return product;
    }

//    public static OrderRequestDto orderRequestDtoOf(long cartId, String shippingAddress) {
//        return new OrderRequestDto(cartId, shippingAddress);
//    }
}
