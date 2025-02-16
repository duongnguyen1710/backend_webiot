package com.datn.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.datn.entity.Orders;
import org.springframework.data.repository.query.Param;

public interface OrdersRepository extends JpaRepository<Orders, Long> {
	public List<Orders> findByCustomerId(Long userId);
	
	public List<Orders> findByRestaurantId(Long restaurantId);
	
	Page<Orders> findByCustomerId(Long customerId, Pageable pageable);
	
	@Query("SELECT SUM(o.totalPrice) FROM Orders o")
    Long calculateTotalRevenue();
	
	@Query("SELECT COUNT(o) FROM Orders o")
    Long countTotalOrders();

	@Query("SELECT o FROM Orders o " +
			"WHERE (:startDate IS NULL OR o.createAt >= :startDate) " +
			"AND (:endDate IS NULL OR o.createAt <= :endDate)")
	Page<Orders> findByCreateAtBetweenOptional(
			@Param("startDate") Date startDate,
			@Param("endDate") Date endDate,
			Pageable pageable
	);


}
