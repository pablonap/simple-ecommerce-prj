package com.agileengine.dto;

import com.agileengine.exception.ExceptionMessages;
import com.agileengine.exception.RequestValidationException;

import java.math.BigDecimal;

public record ProductCreateOrUpdateDto(
    String name,
    String code,
    String description,
    BigDecimal price
) {
    public ProductCreateOrUpdateDto {
        if (name == null
            || name.isEmpty()
            || code == null
            || code.isEmpty()
            || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RequestValidationException(ExceptionMessages.INVALID_PRODUCT_DATA.getMessage());
        }
    }
}
