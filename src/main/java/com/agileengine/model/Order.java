package com.agileengine.model;

import com.agileengine.dto.OrderUpdateDto;
import com.agileengine.exception.ExceptionMessages;
import com.agileengine.exception.InconsistentDataException;
import jakarta.persistence.*;
import org.springframework.lang.NonNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "create_at")
    private LocalDateTime createAt;

    @Column(name = "shipping_address")
    private String shippingAddress;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrderItem> orderItems;

    @Enumerated(EnumType.STRING)
    private State state;

    public Order() {
    }

    public static Order createNewOrder(String shippingAddress) {
        final var order = new Order();
        order.setCreateAt(LocalDateTime.now());
        order.setShippingAddress(shippingAddress);
        order.setOrderItems(new HashSet<>());
        order.setState(State.ON_PROCESS);
        return order;
    }

    public void updateOrder(OrderUpdateDto dto) {
        this.setShippingAddress(dto.shippingAddress());
        this.setState(dto.state());
    }

    public BigDecimal calculateTotalAmount() {
        if (this.getOrderItems() == null
            || this.getOrderItems().isEmpty()) {
            throw new InconsistentDataException(ExceptionMessages.NO_ORDER_ITEMS.getMessage());
        }
        return this.getOrderItems()
            .stream()
            .map(oi -> oi.getProduct().getPrice().multiply(BigDecimal.valueOf(oi.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Set<OrderItem> getOrderItems() {
        return Set.copyOf(orderItems);
    }

    public void setOrderItems(@NonNull Set<OrderItem> orderItems) {
        this.orderItems = new HashSet<>(orderItems);
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Order order = (Order) o;

        if (id != order.id) return false;
        return Objects.equals(shippingAddress, order.shippingAddress);
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (shippingAddress != null ? shippingAddress.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Order{" +
            "id=" + id +
            ", createAt=" + createAt +
            ", shippingAddress='" + shippingAddress + '\'' +
            ", totalAmount=" + totalAmount +
            ", orderItems=" + orderItems +
            '}';
    }
}
