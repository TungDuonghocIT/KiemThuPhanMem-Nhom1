package com.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.book.model.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
    @Query("SELECT COALESCE(SUM(oi.quantity), 0) FROM OrderItem oi WHERE oi.order.status != 'CANCELLED'")
    Integer getTotalBooksSold();
}