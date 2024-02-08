package com.agileengine.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    public ProductService(ProductRepository productRepository,
                          ProductIdDtoMapper productIdDtoMapper,
                          ProductDtoMapper productDtoMapper) {
        this.productRepository = productRepository;
        this.productIdDtoMapper = productIdDtoMapper;
        this.productDtoMapper = productDtoMapper;
    }

    public ProductDto getById(long productId) {
        logger.info("Getting product by ID: {}", productId);
        final var productFromDb = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessages.RESOURCE_NOT_FOUND.getMessage()));
        logger.info("Retrieved product: {}", productFromDb);
        return productDtoMapper.apply(productFromDb);
    }

    public Page<ProductDto> getProducts(Pageable pageable) {
        logger.info("Getting products...");
        Page<Product> productPage = productRepository.findAll(pageable);
        List<ProductDto> products = productPage.getContent().stream()
            .map(productDtoMapper)
            .collect(Collectors.toList());
        logger.info("Retrieved {} products", products.size());
        return new PageImpl<>(products, pageable, productPage.getTotalElements());
    }

    public ProductIdDto create(ProductCreateOrUpdateDto dto) {
        logger.info("Creating product...");
        final var product = Product.createNewProduct(dto.name(), dto.code(), dto.description(), dto.price());
        final var savedProduct = productRepository.save(product);
        logger.info("Product created successfully with ID: {}", savedProduct.getId());
        return productIdDtoMapper.apply(savedProduct);
    }

    public ProductIdDto update(long productId, ProductCreateOrUpdateDto dto) {
        logger.info("Updating product...");
        final var productFromDb = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessages.RESOURCE_NOT_FOUND.getMessage()));

        final var product = Product.createNewProduct(dto.name(), dto.code(), dto.description(), dto.price());
        product.setId(productFromDb.getId());

        Product savedProduct = productRepository.save(product);
        logger.info("Product successfully updated with ID: {}", savedProduct.getId());
        return productIdDtoMapper.apply(savedProduct);
    }

    public void remove(long productId) {
        logger.info("Removing product with ID: {}", productId);
        productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessages.RESOURCE_NOT_FOUND.getMessage()));
        productRepository.deleteById(productId);
        logger.info("Product removed");
    }
}
