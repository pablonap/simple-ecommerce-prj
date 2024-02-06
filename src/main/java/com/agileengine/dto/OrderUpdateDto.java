package com.agileengine.dto;

import com.agileengine.exception.ExceptionMessages;
import com.agileengine.exception.RequestValidationException;
import com.agileengine.model.State;

public record OrderUpdateDto(
    String shippingAddress,
    State state
) {
    public OrderUpdateDto {
        if (shippingAddress == null
            || shippingAddress.isEmpty()
            || state == null) {
            throw new RequestValidationException(ExceptionMessages.INVALID_ORDER_DATA.getMessage());
        }
    }
}
