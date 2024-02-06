package com.agileengine.model;

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

    @Column(name="create_at")
    private LocalDateTime createAt;

    @Column(name="shipping_address")
    private String shippingAddress;

    @Column(name="total_amount")
    private BigDecimal totalAmount;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrderItem> orderItems;

    public Order() {
    }

    public Order(LocalDateTime createAt, String shippingAddress, BigDecimal totalAmount, Set<OrderItem> orderItems) {
        this.createAt = createAt;
        this.shippingAddress = shippingAddress;
        this.totalAmount = totalAmount;
        this.orderItems = orderItems;
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
