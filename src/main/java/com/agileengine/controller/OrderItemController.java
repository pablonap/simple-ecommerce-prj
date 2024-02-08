package com.agileengine.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.agileengine.dto.OrderItemCreateOrUpdateDto;
import com.agileengine.dto.OrderItemDto;
import com.agileengine.dto.OrderItemIdDto;
import com.agileengine.service.OrderItemService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order-items")
public class OrderItemController {
    private final OrderItemService orderItemService;
    private static final Logger logger = LoggerFactory.getLogger(OrderItemController.class);

    public OrderItemController(OrderItemService orderItemService) {
        this.orderItemService = orderItemService;
    }

    @PostMapping()
    public ResponseEntity<OrderItemIdDto> create(@RequestBody OrderItemCreateOrUpdateDto dto) {
        logger.info("Received request to create order item with DTO: {}", dto);
        return new ResponseEntity<>(orderItemService.create(dto), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderItemIdDto> update(
        @PathVariable("id") long orderId,
        @RequestBody OrderItemCreateOrUpdateDto dto) {
        logger.info("Received request to update order item with ID: {} and DTO: {}", orderId, dto);
        return new ResponseEntity<>(orderItemService.update(orderId, dto), HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<Page<OrderItemDto>> findAll(
        @RequestParam(name = "page", defaultValue = "0", required = false) int page,
        @RequestParam(name = "size", defaultValue = "30", required = false) int size) {
        logger.info("Received request to find all order items with page: {} and size: {}", page, size);
        return new ResponseEntity<>(
            orderItemService.getOrderItems(PageRequest.of(page, size)),
            HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderItemDto> findById(@PathVariable("id") long orderItemId) {
        logger.info("Received request to find order item by ID: {}", orderItemId);
        return new ResponseEntity<>(
            orderItemService.getById((orderItemId)), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remove(@PathVariable("id") long orderItemId) {
        logger.info("Received request to remove order item with ID: {}", orderItemId);
        orderItemService.remove(orderItemId);
        return ResponseEntity.ok().build();
    }
}
