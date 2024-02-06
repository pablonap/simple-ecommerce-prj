package com.agileengine.dto;

import com.google.common.base.Preconditions;

public record OrderCreateDto(
    String shippingAddress
) {
  public OrderCreateDto {
    Preconditions.checkNotNull(shippingAddress);
    Preconditions.checkArgument(!shippingAddress.isEmpty());
  }
}
