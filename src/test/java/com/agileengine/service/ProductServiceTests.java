package com.agileengine.service;

import com.agileengine.dto.ProductDtoMapper;
import com.agileengine.dto.ProductIdDtoMapper;
import com.agileengine.exception.RequestValidationException;
import com.agileengine.model.Product;
import com.agileengine.repository.ProductRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTests {
    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductIdDtoMapper productIdDtoMapper;

    @Mock
    private ProductDtoMapper productDtoMapper;

    @InjectMocks
    private ProductService underTest;

    @Test
    void itShouldCreateProduct() {
        // given
        final Long ID = 1L;
        final String NAME = "laptop";
        final String CODE = "1234";
        final String DESCRIPTION = "xxx";
        final BigDecimal PRICE = BigDecimal.valueOf(1200);

        final var productCreateOrUpdateDto = TestsUtils.productCreateOrUpdateDtoOf(NAME, CODE, DESCRIPTION, PRICE);
        final var savedProduct = TestsUtils.productOf(ID, NAME, CODE, DESCRIPTION, PRICE);

        when(productRepository.save(Mockito.any(Product.class))).thenReturn(savedProduct);

        // when
        underTest.create(productCreateOrUpdateDto);

        // then
        ArgumentCaptor<Product> productArgumentCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(productArgumentCaptor.capture());
        Product capturedProduct = productArgumentCaptor.getValue();

        assertThat(savedProduct.getId()).isEqualTo(ID);

        assertThat(capturedProduct.getName()).isEqualTo(productCreateOrUpdateDto.name());
        assertThat(capturedProduct.getCode()).isEqualTo(productCreateOrUpdateDto.code());
        assertThat(capturedProduct.getDescription()).isEqualTo(productCreateOrUpdateDto.description());
        assertThat(capturedProduct.getPrice()).isEqualTo(productCreateOrUpdateDto.price());
    }
}
