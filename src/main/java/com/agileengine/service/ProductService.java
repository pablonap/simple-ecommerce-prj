package com.agileengine.service;

import com.agileengine.dto.*;
import com.agileengine.exception.ExceptionMessages;
import com.agileengine.exception.ResourceNotFoundException;
import com.agileengine.model.Product;
import com.agileengine.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductIdDtoMapper productIdDtoMapper;
    private final ProductDtoMapper productDtoMapper;

    public ProductService(ProductRepository productRepository,
                          ProductIdDtoMapper productIdDtoMapper,
                          ProductDtoMapper productDtoMapper) {
        this.productRepository = productRepository;
        this.productIdDtoMapper = productIdDtoMapper;
        this.productDtoMapper = productDtoMapper;
    }

    public ProductDto getById(long productId) {
        Product productFromDb = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessages.RESOURCE_NOT_FOUND.getMessage()));
        return productDtoMapper.apply(productFromDb);
    }

    public Page<ProductDto> getProducts(Pageable pageable) {
        Page<Product> productPage = productRepository.findAll(pageable);
        List<ProductDto> products = productPage.getContent().stream()
            .map(productDtoMapper)
            .collect(Collectors.toList());
        return new PageImpl<>(products, pageable, productPage.getTotalElements());
    }

    public ProductIdDto create(ProductCreateOrUpdateDto dto) {
        var product = Product.createNewProduct(dto.name(), dto.code(), dto.description(), dto.price());
        Product savedProduct = productRepository.save(product);
        return productIdDtoMapper.apply(savedProduct);
    }

    public ProductIdDto update(long productId, ProductCreateOrUpdateDto dto) {
        Product productFromDb = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessages.RESOURCE_NOT_FOUND.getMessage()));

        var product = Product.createNewProduct(dto.name(), dto.code(), dto.description(), dto.price());
        product.setId(productFromDb.getId());

        Product savedProduct = productRepository.save(product);
        return productIdDtoMapper.apply(savedProduct);
    }

    public void remove(long productId) {
        productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessages.RESOURCE_NOT_FOUND.getMessage()));
        productRepository.deleteById(productId);
    }
}
