package com.agileengine.dto;

public record OrderItemCreateOrUpdateDto(
    long productId,
    long orderId,
    int quantity
) {}
