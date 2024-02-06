package com.agileengine.dto;

import java.math.BigDecimal;

public record ProductDto(
    long productId,
    String name,
    String code,
    String description,
    BigDecimal price
) {}
