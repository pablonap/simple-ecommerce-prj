package com.agileengine.dto;

import com.agileengine.model.OrderItem;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class OrderItemToOrderItemDtoMapper implements Function<OrderItem, OrderItemDto> {
    @Override
    public OrderItemDto apply(OrderItem orderItem) {
        return new OrderItemDto(
            orderItem.getProduct().getId(),
            orderItem.getOrder().getId(),
            orderItem.getQuantity());
    }
}
