package com.agileengine.controller;

import com.agileengine.TestsUtils;
import com.agileengine.dto.ProductCreateOrUpdateDto;
import com.agileengine.dto.ProductDto;
import com.agileengine.dto.ProductIdDto;
import com.agileengine.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductControllerTests {
    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController underTest;

    @Test
    public void itShouldCreateProductWithStatusOk() {
        // given
        final String NAME = "laptop";
        final String CODE = "1234";
        final String DESCRIPTION = "xxx";
        final BigDecimal PRICE = BigDecimal.valueOf(1200);

        final long PRODUCT_ID = 1;

        final var REQUEST = TestsUtils.productCreateOrUpdateDtoOf(NAME, CODE, DESCRIPTION, PRICE);
        final var PRODUCT_ID_DTO_RESPONSE = TestsUtils.productIdDtoOf(PRODUCT_ID);

        when(productService.create(any(ProductCreateOrUpdateDto.class))).thenReturn(PRODUCT_ID_DTO_RESPONSE);

        // when
        ResponseEntity<ProductIdDto> response = underTest.create(REQUEST);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, Objects.requireNonNull(response.getBody()).productId());
    }

    @Test
    public void itShouldUpdateProductWithStatusOk() {
        // given
        final String NAME = "laptop";
        final String CODE = "1234";
        final String DESCRIPTION = "xxx";
        final BigDecimal PRICE = BigDecimal.valueOf(1200);

        final long PRODUCT_ID = 1;

        final var REQUEST = TestsUtils.productCreateOrUpdateDtoOf(NAME, CODE, DESCRIPTION, PRICE);
        final var PRODUCT_ID_DTO_RESPONSE = TestsUtils.productIdDtoOf(PRODUCT_ID);

        when(productService.update(PRODUCT_ID, REQUEST)).thenReturn(PRODUCT_ID_DTO_RESPONSE);

        // when
        ResponseEntity<ProductIdDto> response = underTest.update(PRODUCT_ID, REQUEST);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void itShouldFindAllProducts() {
        // given
        int page = 0;
        int size = 30;
        PageRequest pageRequest = PageRequest.of(page, size);

        List<ProductDto> productDtoList = List.of(
            new ProductDto(1L, "Laptop", "1234", "xxx", BigDecimal.valueOf(1200)),
            new ProductDto(2L, "Phone", "5678", "zzz", BigDecimal.valueOf(800))
        );

        Page<ProductDto> productDtoPage =
            new PageImpl<>(productDtoList, pageRequest, productDtoList.size());

        when(productService.getProducts(pageRequest)).thenReturn(productDtoPage);

        // when
        ResponseEntity<Page<ProductDto>> response = underTest.findAll(page, size);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(productDtoPage, response.getBody());
    }

    @Test
    public void itShouldFindProductById() {
        // given
        long productId = 1L;

        ProductDto productDto = new ProductDto(productId, "Laptop", "1234", "Description", BigDecimal.valueOf(1200));

        when(productService.getById(anyLong())).thenReturn(productDto);

        // when
        ResponseEntity<ProductDto> response = underTest.findById(productId);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(productDto, response.getBody());
    }

    @Test
    public void itShouldRemoveProductById() {
        // given
        long productId = 1L;

        // when
        ResponseEntity<Void> response = underTest.remove(productId);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(productService).remove(productId);
    }
}
