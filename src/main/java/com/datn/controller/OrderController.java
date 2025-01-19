package com.datn.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.datn.entity.Orders;
import com.datn.entity.User;
import com.datn.repository.OrdersRepository;
import com.datn.request.OrderRequest;
import com.datn.response.StripeResponse;
import com.datn.response.VnPayResponse;
import com.datn.service.OrderService;
import com.datn.service.StripeService;
import com.datn.service.UserService;
import com.datn.service.VnPayService;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class OrderController {
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private StripeService stripeService;
//	
	@Autowired
	private UserService userService;
	
	@Autowired
	private OrdersRepository ordersRepository;
//	
	@Autowired
	private VnPayService vnPayService;
	
//	@Autowired
//	private PaypalServices paypalServices;
//	
//	@Autowired
//	private ZaloPayService zaloPayService;
//	
//	@Autowired
//	private MomoService momoService;
	//VNPay
//	@PostMapping("/order")
//	public ResponseEntity<VNPayResponse> createOrder(@RequestBody OrderRequest req,
//			@RequestHeader("Authorization") String jwt) throws Exception {
//		User user = userService.findUserByJwtToken(jwt);
//		Order order = orderService.createOrder(req, user);
//		VNPayResponse res = vnPayService.createVNPayLink(order);
//		//PaymentResponse res=paymentService.createPaymentLink(order);
//		return new ResponseEntity<>(res, HttpStatus.OK);
//	}
	// Stripe
//	@PostMapping("/order")
//	public ResponseEntity<PaymentResponse> createOrder(@RequestBody OrderRequest req,
//			@RequestHeader("Authorization") String jwt) throws Exception {
//		User user = userService.findUserByJwtToken(jwt);
//		Order order = orderService.createOrder(req, user);
//		//VNPayResponse res = vnPayService.createVNPayLink(order);
//		PaymentResponse res=paymentService.createPaymentLink(order);
//		return new ResponseEntity<>(res, HttpStatus.OK);
//	}
	
	@PostMapping("/orders")
	public ResponseEntity<?> createOrder(@RequestBody OrderRequest req,
	                                     @RequestHeader("Authorization") String jwt) throws Exception {
	    User user = userService.findUserByJwtToken(jwt);
	    Orders order = orderService.createOrder(req, user);

	    switch (req.getPaymentMethod().toLowerCase()) {
	        case "vnpay":
	            order.setPaymentType(1); // 1: VNPay
	            ordersRepository.save(order); // Lưu đơn hàng với paymentType đã cập nhật
	            VnPayResponse vnPayRes = vnPayService.createVNPayLink(order);
	            return new ResponseEntity<>(vnPayRes, HttpStatus.OK);

	        case "stripe":
	            order.setPaymentType(3); // 3: Stripe
	            ordersRepository.save(order); // Lưu đơn hàng với paymentType đã cập nhật
	            StripeResponse stripeRes = stripeService.createPaymentLink(order);
	            return new ResponseEntity<>(stripeRes, HttpStatus.OK);

	        case "cod":
	            order.setPaymentType(4); // 4: COD
	            ordersRepository.save(order); // Lưu đơn hàng với paymentType đã cập nhật
	            return new ResponseEntity<>("Order placed successfully. Payment on delivery (COD).", HttpStatus.OK);

	        default:
	            return new ResponseEntity<>("Invalid payment method", HttpStatus.BAD_REQUEST);
	    }
	}
	@GetMapping("/orders/user")
	public ResponseEntity<List<Orders>> getOrderHistory(
			@RequestHeader("Authorization") String jwt) throws Exception {
		User user = userService.findUserByJwtToken(jwt);
		List<Orders> orders = orderService.getUsersOrder(user.getId());
		return new ResponseEntity<>(orders, HttpStatus.OK);
	}
	
	@GetMapping("/orders/userss")
	 public ResponseEntity<Page<Orders>> getOrderHistory(
	         @RequestHeader("Authorization") String jwt,
	         @RequestParam(defaultValue = "0") int page,
	         @RequestParam(defaultValue = "10") int size) throws Exception {
	     User user = userService.findUserByJwtToken(jwt);
	     Page<Orders> orders = orderService.getUsersOrder(user.getId(), PageRequest.of(page, size));
	     return new ResponseEntity<>(orders, HttpStatus.OK);
	 }
}
