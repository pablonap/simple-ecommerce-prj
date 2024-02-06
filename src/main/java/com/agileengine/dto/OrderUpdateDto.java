package com.agileengine.dto;

import com.agileengine.model.State;

public record OrderUpdateDto(
    String shippingAddress,
    State state
) {}
