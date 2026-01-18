package com.practice.spring.data.spring_jpa_tx.entity;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class OrderItemId implements Serializable {
    public OrderItemId() {
    }

    private Long orderId;
    private Long itemId;

    public OrderItemId(Long orderId, Long itemId) {
        this.orderId = orderId;
        this.itemId = itemId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OrderItemId that = (OrderItemId) o;
        return Objects.equals(orderId, that.orderId) && Objects.equals(itemId, that.itemId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, itemId);
    }
}
