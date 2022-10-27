package com.ortest.repository;

import com.ortest.model.OrderItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItems, Long> {
    List<OrderItems> findAllById(Iterable<Long> longs);

    @Override
    Optional<OrderItems> findById(Long aLong);

    @Query(value = "SELECT COUNT(*) FROM order_items WHERE products_id = :id", nativeQuery = true)
    Long findRelatedItems(Long id);

    @Query(value = "SELECT * FROM order_items WHERE orders_id = :id", nativeQuery = true)
    List<OrderItems> findOrdersTodelete(Long id);
}