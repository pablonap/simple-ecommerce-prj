package com.agileengine.dto;

import com.google.common.base.Preconditions;

import java.math.BigDecimal;

public record ProductCreateOrUpdateDto(
    String name,
    String code,
    String description,
    BigDecimal price
) {
  public ProductCreateOrUpdateDto {
    Preconditions.checkNotNull(name);
    Preconditions.checkArgument(name.isEmpty());
    Preconditions.checkNotNull(code);
    Preconditions.checkArgument(code.isEmpty());
    Preconditions.checkArgument(price.compareTo(BigDecimal.ZERO) > 0);
  }
}
