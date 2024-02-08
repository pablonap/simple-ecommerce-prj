package com.agileengine.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.agileengine.dto.*;
import com.agileengine.exception.ExceptionMessages;
import com.agileengine.exception.RequestValidationException;
import com.agileengine.exception.ResourceNotFoundException;
import com.agileengine.model.Order;
import com.agileengine.model.State;
import com.agileengine.repository.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderToLongMapper orderToLongMapper;
    private final OrderToOrderDtoMapper orderToOrderDtoMapper;
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    public OrderService(OrderRepository orderRepository,
                        OrderToLongMapper orderToLongMapper,
                        OrderItemToLongMapper orderItemToLongMapper,
                        OrderToOrderDtoMapper orderToOrderDtoMapper) {
        this.orderRepository = orderRepository;
        this.orderToLongMapper = orderToLongMapper;
        this.orderToOrderDtoMapper = orderToOrderDtoMapper;
    }

    public OrderIdDto create(OrderCreateDto request) {
        logger.info("Creating new order...");
        Order order = Order.createNewOrder(request.shippingAddress());
        Order savedOrder = orderRepository.save(order);
        logger.info("Order created successfully with ID: {}", savedOrder.getId());
        return new OrderIdDto(orderToLongMapper.apply(savedOrder));
    }

    public OrderIdDto update(long orderId, OrderUpdateDto request) {
        logger.info("Updating order with ID: {}", orderId);
        Order orderFromDb = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessages.RESOURCE_NOT_FOUND.getMessage()));

        if (orderFromDb.getState().equals(State.FINISHED)) {
            logger.warn("Order with ID {} is already finished. Update not allowed.", orderId);
            throw new RequestValidationException(ExceptionMessages.ORDER_ALREADY_FINISHED.getMessage());
        }

        orderFromDb.updateOrder(request);

        if (request.state().equals(State.FINISHED)) {
            orderFromDb.setTotalAmount(orderFromDb.calculateTotalAmount());
        }

        Order updatedOrder = orderRepository.save(orderFromDb);
        logger.info("Order updated successfully with ID: {}", orderId);
        return new OrderIdDto(orderToLongMapper.apply(updatedOrder));
    }

    public OrderDto getById(long orderId) {
        logger.info("Fetching order details for order with ID: {}", orderId);
        Order orderFromDb = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessages.RESOURCE_NOT_FOUND.getMessage()));
        return orderToOrderDtoMapper.apply(orderFromDb);
    }

    public Page<OrderDto> getOrders(Pageable pageable) {
        logger.info("Fetching orders...");
        Page<Order> orderPage = orderRepository.findAll(pageable);
        List<OrderDto> orders = orderPage.getContent().stream()
            .map(orderToOrderDtoMapper)
            .collect(Collectors.toList());
        logger.info("Fetched {} orders", orders.size());
        return new PageImpl<>(orders, pageable, orderPage.getTotalElements());
    }

    public void remove(long orderId) {
        logger.info("Removing order with ID: {}", orderId);
        orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessages.RESOURCE_NOT_FOUND.getMessage()));
        orderRepository.deleteById(orderId);
        logger.info("Order removed successfully with ID: {}", orderId);
    }
}
