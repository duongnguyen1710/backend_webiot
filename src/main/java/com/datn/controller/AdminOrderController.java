package com.datn.controller;

import com.datn.repository.OrdersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.datn.entity.Orders;
import com.datn.service.OrderService;

import java.util.Date;

@RestController
@CrossOrigin
@RequestMapping("/api/admin/orders")
public class AdminOrderController {
	@Autowired
	private OrderService orderService;

	@Autowired
	private OrdersRepository ordersRepository;

	@GetMapping
	public Page<Orders> getAllOrdersPaginated(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		// Thêm sắp xếp giảm dần theo createAt
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createAt"));
		return orderService.getAllOrders(pageable);
	}

	@GetMapping("/{id}")
	public Orders getOrderById(@PathVariable Long id) {
		return orderService.getOrderById(id);
	}

	@PutMapping("/{orderId}/status")
	public ResponseEntity<Orders> updateOrderStatus(@RequestHeader("Authorization") String jwt,
			@PathVariable Long orderId, @RequestParam String status) {

		// Gọi service để cập nhật trạng thái
		Orders updatedOrder = orderService.updateOrderStatus(orderId, status);

		// Trả về response
		return ResponseEntity.ok(updatedOrder);
	}

	@GetMapping("/total-revenue")
	public ResponseEntity<Long> getTotalRevenue() {
		Long totalRevenue = orderService.getTotalRevenue();
		return ResponseEntity.ok(totalRevenue);
	}
	
	@GetMapping("/total-orders")
    public ResponseEntity<Long> getTotalOrders() {
        Long totalOrders = orderService.getTotalOrders();
        return ResponseEntity.ok(totalOrders);
    }

	@GetMapping("/filter-by-date")
	public Page<Orders> filterOrdersByDate(
			@RequestParam(value = "startDate", required = false)
			@DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,

			@RequestParam(value = "endDate", required = false)
			@DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,

			Pageable pageable) {

		return ordersRepository.findByCreateAtBetweenOptional(startDate, endDate, pageable);
	}
}
