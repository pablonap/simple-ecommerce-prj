package com.agileengine.dto;

import com.agileengine.TestsUtils;
import com.agileengine.exception.RequestValidationException;
import com.agileengine.model.State;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class OrderUpdateDtoTests {
    @Test
    public void itShouldNotCreateOrderUpdateDtoWithEmptyShippingAddress() {
        // given
        final String SHIPPING_ADDRESS_EMPTY = "";
        final State STATE = State.ON_PROCESS;

        // when and then
        assertThrows(RequestValidationException.class, () -> TestsUtils.orderUpdateDtoOf(SHIPPING_ADDRESS_EMPTY, STATE));
    }

    @Test
    public void itShouldNotCreateOrderUpdateDtoWithNullState() {
        // given
        final String SHIPPING_ADDRESS_EMPTY = "x street";
        final State STATE = null;

        // when and then
        assertThrows(RequestValidationException.class, () -> TestsUtils.orderUpdateDtoOf(SHIPPING_ADDRESS_EMPTY, STATE));
    }

    @Test
    public void itShouldCreateOrderUpdateDto() {
        // given
        final String SHIPPING_ADDRESS = "x street";
        final State STATE = State.ON_PROCESS;

        // when
        OrderUpdateDto response = TestsUtils.orderUpdateDtoOf(SHIPPING_ADDRESS, STATE);

        // then
        assertThat(SHIPPING_ADDRESS).isEqualTo(response.shippingAddress());
        assertThat(STATE).isEqualTo(response.state());
    }
}
