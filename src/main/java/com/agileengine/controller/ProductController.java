package com.agileengine.controller;

import com.agileengine.dto.ProductCreateOrUpdateDto;
import com.agileengine.dto.ProductDto;
import com.agileengine.dto.ProductIdDto;
import com.agileengine.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping()
    public ResponseEntity<ProductIdDto> create(@RequestBody ProductCreateOrUpdateDto dto) {
        return new ResponseEntity<>(productService.create(dto), HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<Page<ProductDto>> findAll(
        @RequestParam(name = "page", defaultValue = "0", required = false) int page,
        @RequestParam(name = "size", defaultValue = "30", required = false) int size) {
        return new ResponseEntity<>(
            productService.getProducts(PageRequest.of(page, size)),
            HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> findById(@PathVariable("id") long productId) {
        return new ResponseEntity<>(
            productService.getById((productId)), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductIdDto> update(@PathVariable("id") long productId, @RequestBody ProductCreateOrUpdateDto dto) {
        return new ResponseEntity<>(productService.update(productId, dto), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remove(@PathVariable("id") long productId) {
        productService.remove(productId);
        return ResponseEntity.ok().build();
    }
}
