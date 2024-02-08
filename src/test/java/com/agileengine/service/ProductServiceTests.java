package com.agileengine.service;

import com.agileengine.TestsUtils;
import com.agileengine.dto.ProductDtoMapper;
import com.agileengine.dto.ProductIdDto;
import com.agileengine.dto.ProductIdDtoMapper;
import com.agileengine.exception.ResourceNotFoundException;
import com.agileengine.model.Product;
import com.agileengine.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

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
        final Long PRODUCT_ID = 1L;
        final String NAME = "laptop";
        final String CODE = "1234";
        final String DESCRIPTION = "xxx";
        final BigDecimal PRICE = BigDecimal.valueOf(1200);

        final var productCreateOrUpdateDto = TestsUtils.productCreateOrUpdateDtoOf(NAME, CODE, DESCRIPTION, PRICE);
        final var savedProduct = TestsUtils.productOf(PRODUCT_ID, NAME, CODE, DESCRIPTION, PRICE);
        final var productIdDtoMapped = TestsUtils.productIdDtoOf(PRODUCT_ID);

        when(productRepository.save(Mockito.any(Product.class))).thenReturn(savedProduct);
        when(productIdDtoMapper.apply(savedProduct)).thenReturn(productIdDtoMapped);

        // when
        ProductIdDto productIdDto = underTest.create(productCreateOrUpdateDto);

        // then
        ArgumentCaptor<Product> productArgumentCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(productArgumentCaptor.capture());
        Product capturedProduct = productArgumentCaptor.getValue();

        assertThat(savedProduct.getId()).isEqualTo(PRODUCT_ID);
        assertThat(productIdDto.productId()).isEqualTo(PRODUCT_ID);

        assertThat(capturedProduct.getName()).isEqualTo(productCreateOrUpdateDto.name());
        assertThat(capturedProduct.getCode()).isEqualTo(productCreateOrUpdateDto.code());
        assertThat(capturedProduct.getDescription()).isEqualTo(productCreateOrUpdateDto.description());
        assertThat(capturedProduct.getPrice()).isEqualTo(productCreateOrUpdateDto.price());
    }

    @Test
    void itShouldUpdateProduct() {
        // given
        final Long PRODUCT_ID = 1L;
        final String NAME = "laptop";
        final String CODE = "1234";
        final String DESCRIPTION = "xxx";
        final BigDecimal PRICE = BigDecimal.valueOf(1200);

        final var productCreateOrUpdateDto = TestsUtils.productCreateOrUpdateDtoOf(NAME, CODE, DESCRIPTION, PRICE);
        final var productFromDb = TestsUtils.productOf(PRODUCT_ID, NAME, CODE, DESCRIPTION, PRICE);
        final var productIdDtoMapped = TestsUtils.productIdDtoOf(PRODUCT_ID);
        final var updatedProduct = TestsUtils.productOf(PRODUCT_ID, NAME, CODE, DESCRIPTION, PRICE);

        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(productFromDb));
        when(productRepository.save(Mockito.any(Product.class))).thenReturn(updatedProduct);
        when(productIdDtoMapper.apply(updatedProduct)).thenReturn(productIdDtoMapped);

        // when
        ProductIdDto productIdDto = underTest.update(PRODUCT_ID, productCreateOrUpdateDto);

        // then
        ArgumentCaptor<Product> productArgumentCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(productArgumentCaptor.capture());
        Product capturedProduct = productArgumentCaptor.getValue();

        assertThat(productIdDto.productId()).isEqualTo(PRODUCT_ID);

        assertThat(capturedProduct.getId()).isEqualTo(PRODUCT_ID);
        assertThat(capturedProduct.getName()).isEqualTo(productCreateOrUpdateDto.name());
        assertThat(capturedProduct.getCode()).isEqualTo(productCreateOrUpdateDto.code());
        assertThat(capturedProduct.getDescription()).isEqualTo(productCreateOrUpdateDto.description());
        assertThat(capturedProduct.getPrice()).isEqualTo(productCreateOrUpdateDto.price());
    }

    @Test
    void itShouldThrowExceptionWhenProductNotFoundWhenTryingToUpdate() {
        // given
        final Long PRODUCT_ID = 1L;
        final String NAME = "laptop";
        final String CODE = "1234";
        final String DESCRIPTION = "xxx";
        final BigDecimal PRICE = BigDecimal.valueOf(1200);

        final var productCreateOrUpdateDto = TestsUtils.productCreateOrUpdateDtoOf(NAME, CODE, DESCRIPTION, PRICE);

        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.empty());

        // when & then
        assertThrows(ResourceNotFoundException.class, () -> underTest.update(PRODUCT_ID, productCreateOrUpdateDto));
        verify(productRepository, never()).save(ArgumentMatchers.any());
    }

    @Test
    void itShouldThrowExceptionWhenProductNotFoundWhenTryingToRemoveById() {
        // given
        final Long PRODUCT_ID = 1L;

        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.empty());

        // when & then
        assertThrows(ResourceNotFoundException.class, () -> underTest.remove(PRODUCT_ID));
        verify(productRepository, never()).deleteById(ArgumentMatchers.any());
    }

    @Test
    void itShouldRemoveProduct() {
        // given
        final Long PRODUCT_ID = 1L;
        final String NAME = "laptop";
        final String CODE = "1234";
        final String DESCRIPTION = "xxx";
        final BigDecimal PRICE = BigDecimal.valueOf(1200);

        final var productFromDb = TestsUtils.productOf(PRODUCT_ID, NAME, CODE, DESCRIPTION, PRICE);

        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(productFromDb));

        // when
        underTest.remove(PRODUCT_ID);

        // then
        ArgumentCaptor<Long> idArgumentCaptor = ArgumentCaptor.forClass(Long.class);
        verify(productRepository).deleteById(idArgumentCaptor.capture());
        Long capturedId = idArgumentCaptor.getValue();

        assertThat(capturedId).isEqualTo(PRODUCT_ID);
    }

    @Test
    void itShouldThrowExceptionWhenProductNotFoundWhenTryingToGetById() {
        // given
        final Long PRODUCT_ID = 1L;

        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.empty());

        // when & then
        assertThrows(ResourceNotFoundException.class, () -> underTest.remove(PRODUCT_ID));
        verify(productRepository, never()).deleteById(ArgumentMatchers.any());
    }

    @Test
    void itShouldGetProductById() {
        // given
        final Long PRODUCT_ID = 1L;
        final String NAME = "laptop";
        final String CODE = "1234";
        final String DESCRIPTION = "xxx";
        final BigDecimal PRICE = BigDecimal.valueOf(1200);

        final var productFromDb = TestsUtils.productOf(PRODUCT_ID, NAME, CODE, DESCRIPTION, PRICE);

        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(productFromDb));

        // when
        underTest.getById(PRODUCT_ID);

        // then
        ArgumentCaptor<Long> idArgumentCaptor = ArgumentCaptor.forClass(Long.class);
        verify(productRepository).findById(idArgumentCaptor.capture());
        Long capturedId = idArgumentCaptor.getValue();

        assertThat(capturedId).isEqualTo(PRODUCT_ID);
    }

    @Test
    void itShouldGetProducts() {
        // given
        final Long PRODUCT_ID = 1L;
        final String NAME = "laptop";
        final String CODE = "1234";
        final String DESCRIPTION = "xxx";
        final BigDecimal PRICE = BigDecimal.valueOf(1200);

        final var productFromDb = TestsUtils.productOf(PRODUCT_ID, NAME, CODE, DESCRIPTION, PRICE);
        final var productDto = TestsUtils.productDtoOf(PRODUCT_ID, NAME, CODE, DESCRIPTION, PRICE);

        List<Product> products = List.of(productFromDb);
        Page<Product> productsPage = new PageImpl<>(products);
        when(productRepository.findAll(any(Pageable.class))).thenReturn(productsPage);
        when(productDtoMapper.apply(productFromDb)).thenReturn(productDto);

        // when
        underTest.getProducts(PageRequest.of(0, 30));

        // then
        ArgumentCaptor<Pageable> pageableArgumentCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(productRepository).findAll(pageableArgumentCaptor.capture());
        Pageable captured = pageableArgumentCaptor.getValue();

        assertThat(captured).isNotNull();
    }
}
