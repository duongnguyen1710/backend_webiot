package com.datn.controller;

import java.util.Collection;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.datn.request.UpdatePasswordRequest;
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

	@PostMapping("/resend")
	public ResponseEntity<String> resendOtp(@RequestBody VerifiedEmailRequest verifiedEmailRequest) {
		String email = verifiedEmailRequest.getEmail();

		User user = userRepository.findByEmail(email);
		if (user == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Người dùng không tồn tại!");
		}

		String otp = String.format("%06d", new Random().nextInt(999999));

		redisTemplate.opsForValue().set(email, otp, 10, TimeUnit.MINUTES);

		String subject = "Xác thực email - OTP mới";
		String text = "Chào " + user.getFullName() + ",\n\nMã OTP mới của bạn là: " + otp + "\n\nOTP có hiệu lực trong 10 phút.";
		emailService.sendEmail(email, subject, text);

		return ResponseEntity.ok("OTP mới đã được gửi đến email của bạn!");
	}

	@PostMapping("/forgot-password")
	public ResponseEntity<String> forgotPassword(@RequestBody VerifiedEmailRequest request) {
		String email = request.getEmail();

		// Kiểm tra người dùng có tồn tại không
		User user = userRepository.findByEmail(email);
		if (user == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Người dùng không tồn tại!");
		}

		// Kiểm tra xem email đã được xác thực chưa
		if (!user.isVerified()) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Email chưa được xác thực. Vui lòng xác thực email trước khi đặt lại mật khẩu!");
		}

		// Tạo OTP ngẫu nhiên
		String otp = String.format("%06d", new Random().nextInt(999999));

		// Lưu OTP vào Redis với thời gian hết hạn là 10 phút
		redisTemplate.opsForValue().set(email, otp, 10, TimeUnit.MINUTES);

		// Gửi email chứa OTP
		String subject = "Đặt lại mật khẩu - Mã OTP";
		String text = "Chào " + user.getFullName() + ",\n\nMã OTP để đặt lại mật khẩu của bạn là: " + otp + "\n\nOTP có hiệu lực trong 10 phút.";
		emailService.sendEmail(email, subject, text);

		return ResponseEntity.ok("OTP đặt lại mật khẩu đã được gửi đến email của bạn!");
	}

	@PostMapping("/verify-reset-password")
	public ResponseEntity<String> verifyResetPasswordOtp(@RequestBody VerifiedEmailRequest request) {
		String email = request.getEmail();
		String otp = request.getOpt();

		// Lấy OTP từ Redis
		String storedOtp = redisTemplate.opsForValue().get(email);

		// Kiểm tra OTP có hợp lệ không
		if (storedOtp == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Mã OTP đã hết hạn hoặc không tồn tại!");
		}
		if (!storedOtp.equals(otp)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Mã OTP không hợp lệ!");
		}

		// Nếu OTP hợp lệ, xóa OTP khỏi Redis
		redisTemplate.delete(email);

		// Lưu trạng thái đã xác thực OTP vào Redis (có hiệu lực 15 phút)
		redisTemplate.opsForValue().set("verified:" + email, "true", 15, TimeUnit.MINUTES);

		return ResponseEntity.ok("Mã OTP hợp lệ! Bạn có thể đặt lại mật khẩu.");
	}

	@PostMapping("/reset-password")
	public ResponseEntity<String> resetPassword(@RequestBody UpdatePasswordRequest request) {
		String email = request.getEmail();
		String newPassword = request.getNewPassword();

		// Kiểm tra xem email đã được xác thực bằng OTP chưa
		String verificationStatus = redisTemplate.opsForValue().get("verified:" + email);
		if (verificationStatus == null || !verificationStatus.equals("true")) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Bạn chưa xác thực OTP. Vui lòng thực hiện bước xác thực OTP trước!");
		}

		// Tìm người dùng theo email
		User user = userRepository.findByEmail(email);
		if (user == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Người dùng không tồn tại!");
		}

		// Cập nhật mật khẩu mới (đã mã hóa)
		user.setPassword(passwordEncoder.encode(newPassword));
		userRepository.save(user);

		// Xóa trạng thái xác thực OTP khỏi Redis để tránh lặp lại việc đặt mật khẩu
		redisTemplate.delete("verified:" + email);

		return ResponseEntity.ok("Mật khẩu đã được đặt lại thành công!");
	}


}

