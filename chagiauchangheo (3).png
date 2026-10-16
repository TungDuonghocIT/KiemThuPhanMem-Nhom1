package com.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.book.model.Order;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByUser_IdOrderByOrderDateDesc(Integer userId);
    List<Order> findAllByOrderByOrderDateDesc();

    @Query("SELECT MONTH(o.orderDate), SUM(o.totalAmount) FROM Order o WHERE YEAR(o.orderDate) = :year AND o.status != 'CANCELLED' GROUP BY MONTH(o.orderDate)")
    List<Object[]> getRevenueByMonth(@Param("year") int year);

    @Query("SELECT o.status, COUNT(o) FROM Order o GROUP BY o.status")
    List<Object[]> getOrderCountByStatus();

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status != 'CANCELLED'")
    Double getTotalRevenue();
}
