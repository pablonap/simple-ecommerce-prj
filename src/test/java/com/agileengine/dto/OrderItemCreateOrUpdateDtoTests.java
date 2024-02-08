package com.agileengine.dto;

import com.agileengine.TestsUtils;
import com.agileengine.exception.RequestValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class OrderItemCreateOrUpdateDtoTests {
    @Test
    public void itShouldNotCreateOrderItemCreateOrUpdateDtoWithInvalidProductId() {
        // given
        final long PRODUCT_ID = -1;
        final long ORDER_ID = 1;
        final int QUANTITY = 3;

        // when and then
        assertThrows(RequestValidationException.class, () ->
            TestsUtils.orderItemCreateOrUpdateDtoOf(PRODUCT_ID, ORDER_ID, QUANTITY));
    }

    @Test
    public void itShouldNotCreateOrderItemCreateOrUpdateDtoWithInvalidOrderId() {
        // given
        final long PRODUCT_ID = 1;
        final long ORDER_ID = -1;
        final int QUANTITY = 3;

        // when and then
        assertThrows(RequestValidationException.class, () ->
            TestsUtils.orderItemCreateOrUpdateDtoOf(PRODUCT_ID, ORDER_ID, QUANTITY));
    }

    @Test
    public void itShouldNotCreateOrderItemCreateOrUpdateDtoWithInvalidQuantity() {
        // given
        final long PRODUCT_ID = 1;
        final long ORDER_ID = 1;
        final int QUANTITY = -1;

        // when and then
        assertThrows(RequestValidationException.class, () ->
            TestsUtils.orderItemCreateOrUpdateDtoOf(PRODUCT_ID, ORDER_ID, QUANTITY));
    }

    @Test
    public void itShouldCreateOrderItemCreateOrUpdateDto() {
        // given
        final long PRODUCT_ID = 1;
        final long ORDER_ID = 1;
        final int QUANTITY = 1;

        // when
        OrderItemCreateOrUpdateDto response = TestsUtils.orderItemCreateOrUpdateDtoOf(PRODUCT_ID, ORDER_ID, QUANTITY);

        // then
        assertThat(response.productId()).isEqualTo(PRODUCT_ID);
        assertThat(response.orderId()).isEqualTo(ORDER_ID);
        assertThat(response.quantity()).isEqualTo(QUANTITY);
    }
}
