package com.agileengine.service;

import com.agileengine.dto.*;
import com.agileengine.model.Product;
import com.agileengine.repository.ProductRepository;
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
        Product productFromDb = productRepository.findById(productId).orElseThrow(IllegalArgumentException::new);
        return productDtoMapper.apply(productFromDb);
    }

    public List<ProductDto> getProducts() {
        return productRepository.findAll()
            .stream()
            .map(productDtoMapper)
            .collect(Collectors.toList());
    }

    public ProductIdDto create(ProductCreateOrUpdateDto dto) {
        Product product = new Product(dto.name(), dto.code(), dto.description(), dto.price());
        Product savedProduct = productRepository.save(product);
        return productIdDtoMapper.apply(savedProduct);
    }

    public ProductIdDto update(long productId, ProductCreateOrUpdateDto dto) {
        Product productFromDb = productRepository.findById(productId).orElseThrow(IllegalArgumentException::new);

        Product product = new Product(dto.name(), dto.code(), dto.description(), dto.price());
        product.setId(productFromDb.getId());

        Product savedProduct = productRepository.save(product);
        return productIdDtoMapper.apply(savedProduct);
    }

    public void remove(long productId) {
        productRepository.findById(productId).orElseThrow(IllegalArgumentException::new);
        productRepository.deleteById(productId);
    }
}
