package com.agileengine.dto;

import com.agileengine.TestsUtils;
import com.agileengine.exception.RequestValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class OrderCreateDtoTests {
    @Test
    public void itShouldNotCreateOrderCreateDtoWithEmptyShippingAddress() {
        // given
        final String SHIPPING_ADDRESS_EMPTY = "";

        // when and then
        assertThrows(RequestValidationException.class, () -> TestsUtils.orderCreateDtoOf(SHIPPING_ADDRESS_EMPTY));
    }

    @Test
    public void itShouldNotCreateOrderCreateDtoWithNullShippingAddress() {
        // given
        final String SHIPPING_ADDRESS_NULL = null;

        // when and then
        assertThrows(RequestValidationException.class, () -> TestsUtils.orderCreateDtoOf(SHIPPING_ADDRESS_NULL));
    }

    @Test
    public void itShouldCreateOrderCreateDto() {
        // given
        final String SHIPPING_ADDRESS = "x street";

        // when
        OrderCreateDto response = TestsUtils.orderCreateDtoOf(SHIPPING_ADDRESS);

        // then
        assertThat(SHIPPING_ADDRESS).isEqualTo(response.shippingAddress());
    }
}
