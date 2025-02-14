package com.datn.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.datn.entity.*;
import com.datn.repository.*;
import com.datn.request.ReorderRequest;
import com.datn.request.RetryPaymentRequest;
import com.datn.response.MomoResponse;
import com.datn.response.ZaloPayResponse;
import com.datn.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.datn.request.OrderRequest;
import com.datn.response.StripeResponse;
import com.datn.response.VnPayResponse;

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

	@Autowired
	private OrderItemRepository orderItemRepository;
//	
	@Autowired
	private VnPayService vnPayService;

	@Autowired
	private CartItemRepository cartItemRepository;

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private CartService cartService;

	@Autowired
	private AddressRepository addressRepository;
	
//	@Autowired
//	private PaypalServices paypalServices;
//	
	@Autowired
	private ZaloPayService zaloPayService;

	@Autowired
	private MomoService momoService;
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

			case "zalopay":
				order.setPaymentType(2); // 3: Stripe
				ordersRepository.save(order); // Lưu đơn hàng với paymentType đã cập nhật
				ZaloPayResponse zaloPayResponse= zaloPayService.createZaloPayLink(order);
				return new ResponseEntity<>(zaloPayResponse, HttpStatus.OK);

			case "momo":
				order.setPaymentType(5); // 3: Stripe
				ordersRepository.save(order); // Lưu đơn hàng với paymentType đã cập nhật
				MomoResponse momoResponse = momoService.createMomoPayment(order);
				return new ResponseEntity<>(momoResponse, HttpStatus.OK);

	        case "cod":
	            order.setPaymentType(4); // 4: COD
	            ordersRepository.save(order); // Lưu đơn hàng với paymentType đã cập nhật
	            return new ResponseEntity<>("Order placed successfully. Payment on delivery (COD).", HttpStatus.OK);

	        default:
	            return new ResponseEntity<>("Invalid payment method", HttpStatus.BAD_REQUEST);
	    }
	}

	@PostMapping("/orders/reorder/{orderId}")
	public ResponseEntity<?> reorder(@PathVariable Long orderId,
									 @RequestBody ReorderRequest reorderRequest,
									 @RequestHeader("Authorization") String jwt) throws Exception {
		// Lấy thông tin user từ JWT
		User user = userService.findUserByJwtToken(jwt);

		// Tìm đơn hàng cũ
		Orders oldOrder = ordersRepository.findById(orderId)
				.orElseThrow(() -> new RuntimeException("Order not found"));

		// Kiểm tra trạng thái đơn hàng có phải "Hoàn thành" không
		if (!"Hoàn thành".equalsIgnoreCase(oldOrder.getOrderStatus())) {
			return new ResponseEntity<>("Chỉ có thể mua lại đơn hàng đã hoàn thành!", HttpStatus.BAD_REQUEST);
		}

		// Lấy địa chỉ mới từ request
		Address newAddress = addressRepository.findById(reorderRequest.getAddressId())
				.orElseThrow(() -> new RuntimeException("Địa chỉ không hợp lệ!"));

		// Tạo đơn hàng mới
		Orders newOrder = new Orders();
		newOrder.setCustomer(user);
		newOrder.setCreateAt(new Date());
		newOrder.setOrderStatus("Chưa giải quyết");
		newOrder.setDeliveryAddress(newAddress); // Cập nhật địa chỉ mới
		newOrder.setOrderType(2);
		newOrder.setRestaurant(oldOrder.getRestaurant());

		// Copy danh sách sản phẩm từ đơn hàng cũ
		List<OrderItem> newOrderItems = new ArrayList<>();
		int totalItemCount = 0;
		long totalPrice = 0;

		for (OrderItem oldItem : oldOrder.getItems()) {
			OrderItem newOrderItem = new OrderItem();
			newOrderItem.setProduct(oldItem.getProduct());
			newOrderItem.setQuantity(oldItem.getQuantity());
			newOrderItem.setTotalPrice(oldItem.getTotalPrice());

			newOrderItem = orderItemRepository.save(newOrderItem);
			newOrderItems.add(newOrderItem);

			totalItemCount += oldItem.getQuantity();
			totalPrice += oldItem.getTotalPrice();
		}

		newOrder.setItems(newOrderItems);
		newOrder.setTotalItem(totalItemCount);
		newOrder.setTotalPrice(totalPrice);

		// Xử lý phương thức thanh toán mới
		switch (reorderRequest.getPaymentMethod().toLowerCase()) {
			case "vnpay":
				newOrder.setPaymentType(1); // VNPay
				break;
			case "stripe":
				newOrder.setPaymentType(3); // Stripe
				break;
			case "zalopay":
				newOrder.setPaymentType(2); // ZaloPay
				break;
			case "momo":
				newOrder.setPaymentType(5); // Momo
				break;
			case "cod":
				newOrder.setPaymentType(4); // Thanh toán khi nhận hàng (COD)
				break;
			default:
				return new ResponseEntity<>("Phương thức thanh toán không hợp lệ!", HttpStatus.BAD_REQUEST);
		}

		// Lưu đơn hàng mới vào database
		Orders savedOrder = ordersRepository.save(newOrder);

		// Xử lý thanh toán nếu không phải COD
		switch (reorderRequest.getPaymentMethod().toLowerCase()) {
			case "vnpay":
				VnPayResponse vnPayRes = vnPayService.createVNPayLink(savedOrder);
				return new ResponseEntity<>(vnPayRes, HttpStatus.OK);
			case "stripe":
				StripeResponse stripeRes = stripeService.createPaymentLink(savedOrder);
				return new ResponseEntity<>(stripeRes, HttpStatus.OK);
			case "zalopay":
				ZaloPayResponse zaloPayResponse = zaloPayService.createZaloPayLink(savedOrder);
				return new ResponseEntity<>(zaloPayResponse, HttpStatus.OK);
			case "momo":
				MomoResponse momoResponse = momoService.createMomoPayment(savedOrder);
				return new ResponseEntity<>(momoResponse, HttpStatus.OK);
			case "cod":
				return new ResponseEntity<>("Mua hàng lại thành công! Thanh toán khi nhận hàng.", HttpStatus.OK);
		}

		return new ResponseEntity<>("Có lỗi xảy ra khi xử lý đơn hàng mới!", HttpStatus.INTERNAL_SERVER_ERROR);
	}


	@PostMapping("/orders/retry-payment/{orderId}")
	public ResponseEntity<?> retryPayment(@PathVariable Long orderId,
										  @RequestBody RetryPaymentRequest retryRequest,
										  @RequestHeader("Authorization") String jwt) throws Exception {
		// Lấy thông tin user từ JWT
		User user = userService.findUserByJwtToken(jwt);

		// Tìm đơn hàng theo orderId
		Orders order = ordersRepository.findById(orderId)
				.orElseThrow(() -> new RuntimeException("Order not found"));

		// Kiểm tra xem đơn hàng có thuộc về user không
		if (!order.getCustomer().getId().equals(user.getId())) {
			return new ResponseEntity<>("Bạn không có quyền truy cập đơn hàng này!", HttpStatus.FORBIDDEN);
		}

		// Nếu đơn hàng đã thanh toán thành công (statusPayment = 1), không cần thanh toán lại
		if (order.getStatusPayment() == 1) {
			return new ResponseEntity<>("Đơn hàng đã được thanh toán thành công, không cần thanh toán lại!", HttpStatus.BAD_REQUEST);
		}

		// Xử lý phương thức thanh toán mới từ request
		switch (retryRequest.getPaymentMethod().toLowerCase()) {
			case "vnpay":
				order.setPaymentType(1); // VNPay
				break;
			case "stripe":
				order.setPaymentType(3); // Stripe
				break;
			case "zalopay":
				order.setPaymentType(2); // ZaloPay
				break;
			case "momo":
				order.setPaymentType(5); // Momo
				break;
			case "cod":
				return new ResponseEntity<>("Không thể thanh toán lại bằng COD!", HttpStatus.BAD_REQUEST);
			default:
				return new ResponseEntity<>("Phương thức thanh toán không hợp lệ!", HttpStatus.BAD_REQUEST);
		}

		// Lưu phương thức thanh toán mới vào database
		ordersRepository.save(order);

		// Xử lý thanh toán dựa vào phương thức mới
		switch (retryRequest.getPaymentMethod().toLowerCase()) {
			case "vnpay":
				VnPayResponse vnPayRes = vnPayService.createVNPayLink(order);
				return new ResponseEntity<>(vnPayRes, HttpStatus.OK);
			case "stripe":
				StripeResponse stripeRes = stripeService.createPaymentLink(order);
				return new ResponseEntity<>(stripeRes, HttpStatus.OK);
			case "zalopay":
				ZaloPayResponse zaloPayResponse = zaloPayService.createZaloPayLink(order);
				return new ResponseEntity<>(zaloPayResponse, HttpStatus.OK);
			case "momo":
				MomoResponse momoResponse = momoService.createMomoPayment(order);
				return new ResponseEntity<>(momoResponse, HttpStatus.OK);
			default:
				return new ResponseEntity<>("Có lỗi xảy ra khi xử lý thanh toán!", HttpStatus.INTERNAL_SERVER_ERROR);
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
