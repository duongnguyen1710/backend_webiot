package com.datn.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Date;

import com.datn.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.datn.entity.Address;
import com.datn.entity.Cart;
import com.datn.entity.CartItem;
import com.datn.entity.OrderItem;
import com.datn.entity.Orders;
import com.datn.entity.Restaurant;
import com.datn.entity.User;
import com.datn.request.OrderRequest;

import jakarta.persistence.EntityNotFoundException;

@Service
public class OrderServiceImp implements OrderService {
	@Autowired
	private OrdersRepository orderRepository;
	
	@Autowired
	private OrderItemRepository orderItemRepository;
	
	@Autowired 
	private AddressRepository addressRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RestaurantService restaurantService;

	@Autowired
	private CartItemRepository cartItemRepository;

	@Autowired
	private CartRepository cartRepository;
	
	@Autowired
	private CartService cartService;
	@Override
	public Orders createOrder(OrderRequest order, User user) throws Exception {
		Address shippAddress = order.getDeliveryAddress();

		shippAddress.setCustomer(user);

		Address savedAddress = addressRepository.save(shippAddress);

//		if (!user.getAddresses().contains(savedAddress)) {
//			user.getAddresses().add(savedAddress);
//			userRepository.save(user);
//		}

		Restaurant restaurant = restaurantService.findRestaurantById(order.getRestaurantId());

		Orders createdOrder = new Orders();
		createdOrder.setCustomer(user);
		createdOrder.setCreateAt(new Date());
		createdOrder.setOrderStatus("Chưa giải quyết");
		createdOrder.setDeliveryAddress(savedAddress);
		createdOrder.setOrderType(2);
		createdOrder.setRestaurant(restaurant);

		Cart cart = cartService.findCartByUserId(user.getId());

		List<OrderItem> orderItems = new ArrayList<>();
		int totalItemCount = 0;

		for (CartItem cartItem : cart.getItems()) {
			OrderItem orderItem = new OrderItem();
			orderItem.setProduct(cartItem.getProduct());
			orderItem.setQuantity(cartItem.getQuantity());
			orderItem.setTotalPrice(cartItem.getTotalPrice());

			OrderItem savedOrderItem = orderItemRepository.save(orderItem);
			orderItems.add(savedOrderItem);

			totalItemCount += cartItem.getQuantity();
		}

		Long totalPrice = cartService.calculateCartTotals(cart);

		createdOrder.setItems(orderItems);
		createdOrder.setTotalPrice(totalPrice);
		createdOrder.setTotalItem(totalItemCount);

		Orders savedOrder = orderRepository.save(createdOrder);
		restaurant.getOrders().add(savedOrder);

		// Xoá toàn bộ CartItem của user sau khi tạo đơn hàng
		cartItemRepository.deleteAll(cart.getItems());

		cart.getItems().clear();
		cartRepository.save(cart);


		return createdOrder;
	}

	@Override
	public Orders updateOrder(Long orderId, String orderStatus) throws Exception {
		Orders order = findOrderById(orderId);
		if(orderStatus.equals("Đơn hàng đang được chuẩn bị giao") 
				|| orderStatus.equals("Đã giao hàng") 
				|| orderStatus.equals("Đã hoàn thành")
				|| orderStatus.equals("Chưa giải quyết")
				) {
			order.setOrderStatus(orderStatus);
			return orderRepository.save(order);
		}
		throw new Exception("Chọn trạng thái đơn hàng");
	}

	@Override
	public void calcelOrder(Long orderId) throws Exception {
		
		Orders order = findOrderById(orderId);
		orderRepository.deleteById(orderId);
	}

	@Override
	public List<Orders> getUsersOrder(Long userId) throws Exception {
		// TODO Auto-generated method stub
		return orderRepository.findByCustomerId(userId);
	}

	@Override
	public List<Orders> getRestaurantsOrder(Long restaurantId, String orderStatus) throws Exception {
		List<Orders> orders = orderRepository.findByRestaurantId(restaurantId);
		if(orderStatus!=null) {
			orders = orders.stream().filter(order ->
					order.getOrderStatus().equals(orderStatus)).collect(Collectors.toList());
		}
		return orders;
	}

	@Override
	public Orders findOrderById(Long orderId) throws Exception {
		Optional<Orders> optionalOrder=orderRepository.findById(orderId);
		if(optionalOrder.isEmpty()) {
			throw new Exception("Không tìm thấy đơn hàng");
		}
		return optionalOrder.get();
	}

	@Override
	public Page<Orders> getUsersOrder(Long userId, Pageable pageable) throws Exception {
		// Thêm Sort.Direction.DESC vào Pageable
	    Pageable sortedByCreateAtDesc = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.DESC, "createAt"));
	    return orderRepository.findByCustomerId(userId, sortedByCreateAtDesc);
	}

	@Override
	public Page<Orders> getAllOrders(Pageable pageable) {
		return orderRepository.findAll(pageable).map(order -> {
			order.setDeliveryAddress(null);
			order.setItems(null);
			return order;
		});
	}

	@Override
	public Orders getOrderById(Long id) {
		return orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
	}

	@Override
	public Orders updateOrderStatus(Long orderId, String newStatus) {
		Orders order = orderRepository.findById(orderId)
	            .orElseThrow(() -> new EntityNotFoundException("Order not found with ID: " + orderId));

		// Cập nhật trạng thái
		order.setOrderStatus(newStatus);

		// Lưu vào database
		return orderRepository.save(order);
	}
	
	private String mapPaymentType(int paymentType) {
	    return switch (paymentType) {
	        case 1 -> "VnPay";
	        case 2 -> "Stripe";
	        case 3 -> "Tiền mặt";
	        default -> "Không xác định";
	    };
	}

	@Override
	public Long getTotalRevenue() {
		return orderRepository.calculateTotalRevenue();
	}

	@Override
	public Long getTotalOrders() {
		 return orderRepository.countTotalOrders();
	}
	
}
