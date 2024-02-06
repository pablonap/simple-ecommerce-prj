package com.agileengine.dto;

import com.agileengine.model.State;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

public record OrderDto (
    LocalDateTime createAt,
    String shippingAddress,
    BigDecimal totalAmount,
    Set<Long> orderItemIds,
    State state
) {}
