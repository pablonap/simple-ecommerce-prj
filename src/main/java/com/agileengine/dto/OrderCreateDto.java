package com.agileengine.dto;

import com.agileengine.exception.ExceptionMessages;
import com.agileengine.exception.RequestValidationException;

public record OrderCreateDto(
    String shippingAddress
) {
    public OrderCreateDto {
        if (shippingAddress == null
            || shippingAddress.isEmpty()) {
            throw new RequestValidationException(ExceptionMessages.INVALID_ORDER_DATA.getMessage());
        }
    }
}
