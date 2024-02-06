package com.agileengine.dto;

import com.agileengine.model.Order;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class OrderToLongMapper implements Function<Order, Long> {
    @Override
    public Long apply(Order order) {
        return order.getId();
    }
}
