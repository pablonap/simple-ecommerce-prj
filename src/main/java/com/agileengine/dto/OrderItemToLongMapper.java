package com.agileengine.dto;

import com.agileengine.model.OrderItem;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class OrderItemToLongMapper implements Function<OrderItem, Long> {
    @Override
    public Long apply(OrderItem orderItem) {
        return orderItem.getId();
    }
}
