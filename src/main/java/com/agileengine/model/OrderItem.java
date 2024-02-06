package com.agileengine.model;

import com.google.common.base.Preconditions;
import jakarta.persistence.*;

import static java.util.Objects.requireNonNull;

@Entity
@Table(name = "order_item")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    private int quantity;

    OrderItem() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = requireNonNull(order);
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = requireNonNull(product);
    }

    public int getQuantity() {
        return quantity;
    }

    public void addQuantity(int quantity) {
        Preconditions.checkArgument(quantity > 0);
        this.quantity += quantity;
    }

    public void setQuantity(int quantity) {
        Preconditions.checkArgument(quantity > 0);
        this.quantity = quantity;
    }
}
