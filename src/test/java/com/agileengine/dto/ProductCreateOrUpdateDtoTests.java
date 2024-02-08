package com.agileengine.dto;

import com.agileengine.TestsUtils;
import com.agileengine.exception.RequestValidationException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ProductCreateOrUpdateDtoTests {
    @Test
    public void itShouldNotCreateProductCreateOrUpdateDtoWithInvalidName() {
        // given
        final String NAME = "";
        final String CODE = "123";
        final String DESCRIPTION = "xxx";
        final BigDecimal PRICE = BigDecimal.valueOf(1200);

        // when and then
        assertThrows(RequestValidationException.class, () -> TestsUtils.productCreateOrUpdateDtoOf(NAME, CODE, DESCRIPTION, PRICE));
    }

    @Test
    public void itShouldNotCreateProductCreateOrUpdateDtoWithInvalidCode() {
        // given
        final String NAME = "laptop";
        final String CODE = "";
        final String DESCRIPTION = "xxx";
        final BigDecimal PRICE = BigDecimal.valueOf(1200);

        // when and then
        assertThrows(RequestValidationException.class, () -> TestsUtils.productCreateOrUpdateDtoOf(NAME, CODE, DESCRIPTION, PRICE));
    }

    @Test
    public void itShouldNotCreateProductCreateOrUpdateDtoWithInvalidPrice() {
        // given
        final String NAME = "laptop";
        final String CODE = "123";
        final String DESCRIPTION = "xxx";
        final BigDecimal PRICE = BigDecimal.valueOf(0);

        // when and then
        assertThrows(RequestValidationException.class, () -> TestsUtils.productCreateOrUpdateDtoOf(NAME, CODE, DESCRIPTION, PRICE));
    }

    @Test
    public void itShouldCreateProductCreateOrUpdateDto() {
        // given
        final String NAME = "laptop";
        final String CODE = "123";
        final String DESCRIPTION = "xxx";
        final BigDecimal PRICE = BigDecimal.valueOf(1200);

        // when
        ProductCreateOrUpdateDto request = TestsUtils.productCreateOrUpdateDtoOf(NAME, CODE, DESCRIPTION, PRICE);

        // then
        assertThat(NAME).isEqualTo(request.name());
        assertThat(CODE).isEqualTo(request.code());
        assertThat(DESCRIPTION).isEqualTo(request.description());
        assertThat(PRICE).isEqualTo(request.price());
    }
}
