package com.agileengine;

import com.agileengine.dto.*;
import com.agileengine.model.Order;
import com.agileengine.model.OrderItem;
import com.agileengine.model.Product;
import com.agileengine.model.State;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

public final class TestsUtils {
    private TestsUtils() {
    }

    public static ProductCreateOrUpdateDto productCreateOrUpdateDtoOf
        (String name,
         String code,
         String description,
         BigDecimal price) {
        return new ProductCreateOrUpdateDto(name, code, description, price);
    }

    public static Product productOf(Long id, String name, String code, String description, BigDecimal price) {
        final var product = new Product();
        product.setId(id);
        product.setName(name);
        product.setCode(code);
        product.setDescription(description);
        product.setPrice(price);

        return product;
    }

    public static ProductIdDto productIdDtoOf(Long id) {
        return new ProductIdDto(id);
    }

    public static ProductDto productDtoOf(Long id, String name, String code, String description, BigDecimal price) {
        return new ProductDto(id, name, code, description, price);
    }

    public static OrderCreateDto orderCreateDtoOf(String shippingAddress) {
        return new OrderCreateDto(shippingAddress);
    }

    public static OrderIdDto orderIdDtoOf(long orderId) {
        return new OrderIdDto(orderId);
    }

    public static Order orderOf(
        long id,
        LocalDateTime createAt,
        String shippingAddress,
        BigDecimal totalAmount,
        Set<OrderItem> orderItems,
        State state) {
        final var order = new Order();
        order.setId(id);
        order.setCreateAt(createAt);
        order.setShippingAddress(shippingAddress);
        order.setTotalAmount(totalAmount);
        order.setOrderItems(orderItems);
        order.setState(state);
        return order;
    }

    public static OrderUpdateDto orderUpdateDtoOf(String shippingAddress, State state) {
        return new OrderUpdateDto(shippingAddress, state);
    }

    public static OrderItem orderItemOf(Product product, Order order, int quantity) {
        final var orderItem = new OrderItem();
        orderItem.setProduct(product);
        orderItem.setOrder(order);
        orderItem.setQuantity(quantity);
        return orderItem;
    }

    public static OrderItem orderItemOf(long id, Product product, Order order, int quantity) {
        final var orderItem = new OrderItem();
        orderItem.setId(id);
        orderItem.setProduct(product);
        orderItem.setOrder(order);
        orderItem.setQuantity(quantity);
        return orderItem;
    }

    public static OrderDto orderDtoOf(
        long id,
        LocalDateTime createAt,
        String shippingAddress,
        BigDecimal totalAmount,
        Set<Long> orderItems,
        State state
    ) {
        return new OrderDto(id, createAt, shippingAddress, totalAmount, orderItems, state);
    }

    public static OrderItemCreateOrUpdateDto orderItemCreateOrUpdateDtoOf(long productId, long orderId, int quantity) {
       return new OrderItemCreateOrUpdateDto(productId, orderId, quantity);
    }

    public static OrderItemDto orderItemDtoOf(long productId, long orderId, int quantity) {
        return new OrderItemDto(productId, orderId, quantity);
    }
}
