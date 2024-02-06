package com.agileengine.service;

import com.agileengine.dto.*;
import com.agileengine.exception.ExceptionMessages;
import com.agileengine.exception.ResourceNotFoundException;
import com.agileengine.model.Order;
import com.agileengine.model.OrderItem;
import com.agileengine.model.Product;
import com.agileengine.repository.OrderItemRepository;
import com.agileengine.repository.OrderRepository;
import com.agileengine.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderItemService {
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderItemToLongMapper orderItemToLongMapper;
    private final OrderItemToOrderItemDtoMapper orderItemToOrderItemDtoMapper;

    public OrderItemService(OrderItemRepository orderItemRepository,
                            ProductRepository productRepository,
                            OrderRepository orderRepository,
                            OrderItemToLongMapper orderItemToLongMapper,
                            OrderItemToOrderItemDtoMapper orderItemToOrderItemDtoMapper) {
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.orderItemToLongMapper = orderItemToLongMapper;
        this.orderItemToOrderItemDtoMapper = orderItemToOrderItemDtoMapper;
    }

    public OrderItemIdDto create(OrderItemCreateOrUpdateDto dto) {
        Product product = productRepository.findById(dto.productId())
            .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessages.RESOURCE_NOT_FOUND.getMessage()));
        Order order = orderRepository.findById(dto.orderId())
            .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessages.RESOURCE_NOT_FOUND.getMessage()));
        OrderItem orderItem = OrderItem.createNewOrderItem(product, order, dto.quantity());

        OrderItem savedOrderItem = orderItemRepository.save(orderItem);
        return new OrderItemIdDto(orderItemToLongMapper.apply(savedOrderItem));
    }

    public OrderItemIdDto update(long orderItemId, OrderItemCreateOrUpdateDto dto) {
        Product product = productRepository.findById(dto.productId())
            .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessages.RESOURCE_NOT_FOUND.getMessage()));
        Order order = orderRepository.findById(dto.orderId())
            .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessages.RESOURCE_NOT_FOUND.getMessage()));
        OrderItem orderItemFromDb = orderItemRepository.findById(orderItemId)
            .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessages.RESOURCE_NOT_FOUND.getMessage()));

        orderItemFromDb.updateOrderItem(product, order, dto.quantity());

        orderItemRepository.save(orderItemFromDb);
        return new OrderItemIdDto(orderItemToLongMapper.apply(orderItemFromDb));
    }

    public OrderItemDto getById(long orderItemId) {
        OrderItem orderItemFromDb = orderItemRepository.findById(orderItemId)
            .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessages.RESOURCE_NOT_FOUND.getMessage()));
        return orderItemToOrderItemDtoMapper.apply(orderItemFromDb);
    }

    public Page<OrderItemDto> getOrderItems(Pageable pageable) {
        Page<OrderItem> orderItemPage = orderItemRepository.findAll(pageable);
        List<OrderItemDto> orderItems = orderItemPage.getContent().stream()
            .map(orderItemToOrderItemDtoMapper)
            .collect(Collectors.toList());
        return new PageImpl<>(orderItems, pageable, orderItemPage.getTotalElements());
    }

    public void remove(long orderItemId) {
        orderItemRepository.findById(orderItemId)
            .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessages.RESOURCE_NOT_FOUND.getMessage()));
        orderItemRepository.deleteById(orderItemId);
    }
}
