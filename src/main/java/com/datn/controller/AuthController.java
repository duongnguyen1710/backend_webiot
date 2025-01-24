package com.datn.controller;

import java.util.Collection;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.datn.request.VerifiedEmailRequest;
import com.datn.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.datn.config.JwtProvider;
import com.datn.entity.Cart;
import com.datn.entity.Role;
import com.datn.entity.User;
import com.datn.repository.CartRepository;
import com.datn.repository.UserRepository;
import com.datn.request.LoginRequest;
import com.datn.response.AuthResponse;
import com.datn.service.CustomerUserDetailsService;

@RestController
@CrossOrigin
@RequestMapping("/auth")
public class AuthController {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private JwtProvider jwtProvider;
	@Autowired
	private CustomerUserDetailsService customerUserDetailsService;
	@Autowired
	private CartRepository cartRepository;
	@Autowired
	private EmailService emailService;
	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	
//	public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtProvider jwtProvider,
//			CustomerUserDetailsService customerUserDetailsService, CartRepository cartRepository) {
//		super();
//		this.userRepository = userRepository;
//		this.passwordEncoder = passwordEncoder;
//		this.jwtProvider = jwtProvider;
//		this.customerUserDetailsService = customerUserDetailsService;
//		this.cartRepository = cartRepository;
//	}
	
	@PostMapping("/signup")
	public ResponseEntity<AuthResponse>createUserHandler(@RequestBody User user) throws Exception{
		User isEmailExist = userRepository.findByEmail(user.getEmail());
		if(isEmailExist!=null) {
			throw new Exception("Email đã tồn tại");
		}
		
		User createdUser = new User();
		createdUser.setEmail(user.getEmail());
		createdUser.setFullName(user.getFullName());
		createdUser.setRole(user.getRole());
		createdUser.setPassword(passwordEncoder.encode(user.getPassword()));
		
		User savedUser = userRepository.save(createdUser);
		
		Cart cart = new Cart();
		cart.setCustomer(savedUser);
		cartRepository.save(cart);

		String otp = String.format("%06d", new Random().nextInt(999999));
		redisTemplate.opsForValue().set(user.getEmail(), otp, 10, TimeUnit.MINUTES);

		String subject = "Dương iot";
		String text = "Chào " + savedUser.getFullName() + ",\n\nBạn đã đăng ký thành công trên hệ thống!"+ ",\n\nOTP :"+ otp;
		emailService.sendEmail(savedUser.getEmail(), subject, text);
		
		Authentication authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		String jwt = jwtProvider.generateToken(authentication);
		
		AuthResponse authResponse = new AuthResponse();
		authResponse.setJwt(jwt);
		authResponse.setMessage("Đăng ký thành công");
		authResponse.setRole(savedUser.getRole());
		return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
		
	}
	@PostMapping("/signin")
	public ResponseEntity<AuthResponse> signin(@RequestBody LoginRequest req){
		
		String username = req.getEmail();
		String password = req.getPassword();
		
		
		Authentication authentication = authenticate(username, password);
		Collection<? extends GrantedAuthority>authorities=authentication.getAuthorities();
		String role=authorities.isEmpty()?null:authorities.iterator().next().getAuthority();
		String jwt = jwtProvider.generateToken(authentication);
		
		AuthResponse authResponse = new AuthResponse();
		authResponse.setJwt(jwt);
		authResponse.setMessage("Đăng nhập thành công");
		
		
		
		authResponse.setRole(Role.valueOf(role));
		return new ResponseEntity<>(authResponse, HttpStatus.OK);
		
		
		
	}
	private Authentication authenticate(String username, String password) {
		UserDetails userDetails = customerUserDetailsService.loadUserByUsername(username);
		
		if(userDetails==null) {
			throw new BadCredentialsException("Tên đăng nhập không tồn tại...");
		}
		
		if(!passwordEncoder.matches(password, userDetails.getPassword())) {
			throw new BadCredentialsException("Mật khẩu không tồn tại...");
		}
		return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
	}
	@PostMapping("/verified")
	public String sendOTP(@RequestBody VerifiedEmailRequest verifiedEmailRequest) {
		String email = verifiedEmailRequest.getEmail();
		String otp = verifiedEmailRequest.getOpt();

		String storedOtp = redisTemplate.opsForValue().get(email);
		System.out.println(storedOtp);
		System.out.println(otp);
		if(storedOtp != null && !storedOtp.equals(otp)){
			return "Opt ko hợp lệ";
		}
		User user = userRepository.findByEmail(email);
		if (user != null) {
			user.setVerified(true);
			userRepository.save(user);
			redisTemplate.delete(email);
			return "Email đã được xác thực thành công!";
		} else {
			return "Người dùng không tồn tại!";
		}
	}
}

