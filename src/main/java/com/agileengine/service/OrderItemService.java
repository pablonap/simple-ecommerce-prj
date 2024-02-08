package com.agileengine.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(OrderItemService.class);

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

    public OrderItemIdDto create(OrderItemCreateOrUpdateDto request) {
        logger.info("Creating new order item...");
        Product product = productRepository.findById(request.productId())
            .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessages.RESOURCE_NOT_FOUND.getMessage()));
        Order order = orderRepository.findById(request.orderId())
            .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessages.RESOURCE_NOT_FOUND.getMessage()));
        OrderItem orderItem = OrderItem.createNewOrderItem(product, order, request.quantity());

        OrderItem savedOrderItem = orderItemRepository.save(orderItem);
        logger.info("Order item created successfully with ID: {}", savedOrderItem.getId());
        return new OrderItemIdDto(orderItemToLongMapper.apply(savedOrderItem));
    }

    public OrderItemIdDto update(long orderItemId, OrderItemCreateOrUpdateDto request) {
        logger.info("Updating order item with ID: {}", orderItemId);
        Product product = productRepository.findById(request.productId())
            .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessages.RESOURCE_NOT_FOUND.getMessage()));
        Order order = orderRepository.findById(request.orderId())
            .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessages.RESOURCE_NOT_FOUND.getMessage()));
        OrderItem orderItemFromDb = orderItemRepository.findById(orderItemId)
            .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessages.RESOURCE_NOT_FOUND.getMessage()));

        orderItemFromDb.updateOrderItem(product, order, request.quantity());

        orderItemRepository.save(orderItemFromDb);
        logger.info("Order item updated successfully with ID: {}", orderItemId);
        return new OrderItemIdDto(orderItemToLongMapper.apply(orderItemFromDb));
    }

    public OrderItemDto getById(long orderItemId) {
        logger.info("Fetching order item details for item with ID: {}", orderItemId);
        OrderItem orderItemFromDb = orderItemRepository.findById(orderItemId)
            .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessages.RESOURCE_NOT_FOUND.getMessage()));
        return orderItemToOrderItemDtoMapper.apply(orderItemFromDb);
    }

    public Page<OrderItemDto> getOrderItems(Pageable pageable) {
        logger.info("Fetching order items...");
        Page<OrderItem> orderItemPage = orderItemRepository.findAll(pageable);
        List<OrderItemDto> orderItems = orderItemPage.getContent().stream()
            .map(orderItemToOrderItemDtoMapper)
            .collect(Collectors.toList());
        logger.info("Fetched {} order items", orderItems.size());
        return new PageImpl<>(orderItems, pageable, orderItemPage.getTotalElements());
    }

    public void remove(long orderItemId) {
        logger.info("Removing order item with ID: {}", orderItemId);
        orderItemRepository.findById(orderItemId)
            .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessages.RESOURCE_NOT_FOUND.getMessage()));
        orderItemRepository.deleteById(orderItemId);
        logger.info("Order item removed successfully with ID: {}", orderItemId);
    }
}
