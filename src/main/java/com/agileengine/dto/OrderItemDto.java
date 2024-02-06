package com.agileengine.dto;

public record OrderItemDto(
    long productId,
    long orderId,
    int quantity
) {}
