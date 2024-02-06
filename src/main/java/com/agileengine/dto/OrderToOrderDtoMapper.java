package com.agileengine.dto;

import com.agileengine.model.Order;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public record OrderToOrderDtoMapper(OrderItemToLongMapper orderItemToLongMapper)
    implements Function<Order, OrderDto> {
    @Override
    public OrderDto apply(Order order) {
        Set<Long> orderItemIds = order.getOrderItems()
            .stream()
            .map(orderItemToLongMapper)
            .collect(Collectors.toSet());

        return new OrderDto(
            order.getCreateAt(),
            order.getShippingAddress(),
            order.getTotalAmount(),
            orderItemIds,
            order.getState());
    }
}
