package com.foodorder.repository;

import com.foodorder.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrderId(Long orderId);

    @Query("SELECT oi2.menuItem.id FROM OrderItem oi1 " +
            "JOIN OrderItem oi2 ON oi1.order.id = oi2.order.id " +
            "WHERE oi1.menuItem.id IN :orderedItemIds AND oi2.menuItem.id NOT IN :orderedItemIds " +
            "GROUP BY oi2.menuItem.id ORDER BY COUNT(oi2.menuItem.id) DESC")
    List<Long> findSuggestedItems(@Param("orderedItemIds") List<Long> orderedItemIds);
}

