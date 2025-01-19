package com.datn.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.datn.entity.Cart;
import com.datn.entity.CartItem;
import com.datn.entity.Product;
import com.datn.entity.User;
import com.datn.repository.CartItemRepository;
import com.datn.repository.CartRepository;
import com.datn.repository.CartService;
import com.datn.request.AddCartItemRequest;

@Service
public class CartServiceImpl implements CartService {
	
	@Autowired
	private CartRepository cartRepository;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private CartItemRepository cartItemRepository;
	
	@Autowired
	private ProductService productService;
	
	@Override
	public CartItem addItemToCart(AddCartItemRequest req, String jwt) throws Exception {
		User user = userService.findUserByJwtToken(jwt);
		
		Product product = productService.findProductById(req.getProductId());
		
		Cart cart = cartRepository.findByCustomerId(user.getId());
		
		for(CartItem cartItem : cart.getItems()) {
			if(cartItem.getProduct().equals(product)) {
				int newQuantity = cartItem.getQuantity()+req.getQuantity();
				return updateCartItemQuantity(cartItem.getId(), newQuantity);
			}
		}
		
		CartItem newCartItem = new CartItem();
		newCartItem.setProduct(product);;
		newCartItem.setCart(cart);
		newCartItem.setQuantity(req.getQuantity());
		newCartItem.setTotalPrice(req.getQuantity()*product.getPrice());
		
		CartItem saveCartItem=cartItemRepository.save(newCartItem);
		cart.getItems().add(saveCartItem);
		return saveCartItem;
	}

	@Override
	public CartItem updateCartItemQuantity(Long cartItemId, int quantity) throws Exception {
		Optional<CartItem> cartItemOptinal = cartItemRepository.findById(cartItemId);
		if(cartItemOptinal.isEmpty()) {
			throw new Exception("Không tìm thấy món ăn trong giỏ hàng");
		}
		
		CartItem item = cartItemOptinal.get();
		item.setQuantity(quantity);
		
		item.setTotalPrice(item.getProduct().getPrice()*quantity);
		return cartItemRepository.save(item);
	}

	@Override
	public Cart removeItemFormCart(Long cartItemId, String jwt) throws Exception {
		
		User user = userService.findUserByJwtToken(jwt);
		
		Cart cart = cartRepository.findByCustomerId(user.getId());
		
		Optional<CartItem> cartItemOptinal = cartItemRepository.findById(cartItemId);
		if(cartItemOptinal.isEmpty()) {
			throw new Exception("Không tìm thấy món ăn trong giỏ hàng");
		}
		
		CartItem item = cartItemOptinal.get();
		
		cart.getItems().remove(item);
		return cartRepository.save(cart);
	}

	@Override
	public Long calculateCartTotals(Cart cart) throws Exception {
		Long total = 0L;
		
		for(CartItem cartItem : cart.getItems()) {
			total += cartItem.getProduct().getPrice()*cartItem.getQuantity();
		}
		return total;
	}

	@Override
	public Cart findCartById(Long id) throws Exception {
		Optional<Cart> optionalCart=cartRepository.findById(id);
		if(optionalCart.isEmpty()) {
			throw new Exception("Không tìm thấy giỏ hàng với mã này");
		}
		return optionalCart.get();
	}

	@Override
	public Cart findCartByUserId(Long userId) throws Exception {
//		User user = userService.findUserByJwtToken(jwt);
		Cart cart = cartRepository.findByCustomerId(userId);
		cart.setTotal(calculateCartTotals(cart));
		return cart;
	}

	@Override
	public Cart clearCart(Long userId) throws Exception {
//		User user = userService.findUserByJwtToken(jwt);
		Cart cart = findCartByUserId(userId);
		
		cart.getItems().clear();
		return cartRepository.save(cart);
	}
}
