package com.agileengine.dto;

import com.agileengine.exception.ExceptionMessages;
import com.agileengine.exception.RequestValidationException;

public record OrderItemCreateOrUpdateDto(
    long productId,
    long orderId,
    int quantity
) {
    public OrderItemCreateOrUpdateDto {
        if (productId < 1
            || orderId < 1
            || quantity < 1) {
            throw new RequestValidationException(ExceptionMessages.INVALID_ORDER_ITEM_DATA.getMessage());
        }
    }
}
