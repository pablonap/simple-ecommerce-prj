package com.agileengine.service;

import com.agileengine.dto.*;
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

    public OrderService(OrderRepository orderRepository,
                        OrderToLongMapper orderToLongMapper,
                        OrderItemToLongMapper orderItemToLongMapper,
                        OrderToOrderDtoMapper orderToOrderDtoMapper) {
        this.orderRepository = orderRepository;
        this.orderToLongMapper = orderToLongMapper;
        this.orderToOrderDtoMapper = orderToOrderDtoMapper;
    }

    public OrderIdDto create(OrderCreateDto dto) {
        Order order = Order.createNewOrder(dto.shippingAddress());
        Order savedOrder = orderRepository.save(order);
        return new OrderIdDto(orderToLongMapper.apply(savedOrder));
    }

    public OrderIdDto update(long orderId, OrderUpdateDto dto) {
        Order orderFromDb = orderRepository.findById(orderId).orElseThrow(IllegalArgumentException::new);

        if (orderFromDb.getState().equals(State.FINISHED)) {
            throw new IllegalArgumentException();
        }

        orderFromDb.updateOrder(dto);

        if (dto.state().equals(State.FINISHED)
            && orderFromDb.getOrderItems() != null
            && orderFromDb.getOrderItems().size() > 0) {
            orderFromDb.setTotalAmount(orderFromDb.calculateTotalAmount());
        }

        Order savedOrder = orderRepository.save(orderFromDb);
        return new OrderIdDto(orderToLongMapper.apply(savedOrder));
    }

    public OrderDto getById(long orderId) {
        Order orderFromDb = orderRepository.findById(orderId).orElseThrow(IllegalArgumentException::new);
        return orderToOrderDtoMapper.apply(orderFromDb);
    }

    public Page<OrderDto> getOrders(Pageable pageable) {
        Page<Order> orderPage = orderRepository.findAll(pageable);
        List<OrderDto> orders = orderPage.getContent().stream()
            .map(orderToOrderDtoMapper)
            .collect(Collectors.toList());
        return new PageImpl<>(orders, pageable, orderPage.getTotalElements());
    }

    public void remove(long orderId) {
        orderRepository.findById(orderId).orElseThrow(IllegalArgumentException::new);
        orderRepository.deleteById(orderId);
    }
}