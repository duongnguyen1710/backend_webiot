package com.datn.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.datn.entity.Orders;
import com.datn.entity.User;
import com.datn.request.OrderRequest;

public interface OrderService {
	public Orders createOrder(OrderRequest order, User user) throws Exception;
	
	public Orders updateOrder(Long orderId, String orderStatus) throws Exception;
	
	public void calcelOrder(Long orderId) throws Exception;
	
	public List<Orders> getUsersOrder(Long userId) throws Exception;
	
	public List<Orders> getRestaurantsOrder(Long restaurantId, String orderStatus) throws Exception;
	
	public Orders findOrderById (Long orderId) throws Exception;
	
	public Page<Orders> getUsersOrder(Long userId, Pageable pageable) throws Exception;
	
	public Page<Orders> getAllOrders(Pageable pageable);
	
	public Orders getOrderById(Long id);
	
	public Orders updateOrderStatus(Long orderId, String newStatus);
	
	public Long getTotalRevenue();
	
	 public Long getTotalOrders();
}

