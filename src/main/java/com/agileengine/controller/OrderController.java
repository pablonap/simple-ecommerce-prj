package com.agileengine.controller;

import com.agileengine.dto.OrderCreateDto;
import com.agileengine.dto.OrderDto;
import com.agileengine.dto.OrderIdDto;
import com.agileengine.dto.OrderUpdateDto;
import com.agileengine.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping()
    public ResponseEntity<OrderIdDto> create(@RequestBody OrderCreateDto dto) {
        return new ResponseEntity<>(orderService.create(dto), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderIdDto> update(
        @PathVariable("id") long orderId,
        @RequestBody OrderUpdateDto dto) {
        return new ResponseEntity<>(orderService.update(orderId, dto), HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<Page<OrderDto>> findAll(
        @RequestParam(name = "page", defaultValue = "0", required = false) int page,
        @RequestParam(name = "size", defaultValue = "30", required = false) int size) {
        return new ResponseEntity<>(
            orderService.getOrders(PageRequest.of(page, size)),
            HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> findById(@PathVariable("id") long orderId) {
        return new ResponseEntity<>(
            orderService.getById((orderId)), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remove(@PathVariable("id") long orderId) {
        orderService.remove(orderId);
        return ResponseEntity.ok().build();
    }
}
