package com.practice.spring.data.spring_jpa_tx.repository;

import com.practice.spring.data.spring_jpa_tx.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // Derived query
    List<Order> findByStatus(String status);

    // JPQL (named parameter must be bound)
    @Query("select o from Order o where o.status = :status")
    List<Order> jpqlQuery(@Param("status") String status);

    // Native SQL (positional parameter is fine)
    @Query(value = "select * from orders where status = ?", nativeQuery = true)
    List<Order> nativeQuery(String status);

    // Projection (interface-based)
    interface OrderView {
        Long getOrderId();
        String getStatus();
    }

    // Projection + sorting
    List<OrderView> findByStatusOrderByOrderIdDesc(String status);
}
