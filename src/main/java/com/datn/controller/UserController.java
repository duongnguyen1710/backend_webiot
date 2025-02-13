package com.datn.controller;

import com.datn.config.JwtProvider;
import com.datn.repository.UserRepository;
import com.datn.request.ChangePasswordRequest;
import com.datn.request.UserProfileRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.datn.entity.User;
import com.datn.service.UserService;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin
public class UserController {
	@Autowired
	private UserService userService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JwtProvider jwtProvider;

	@GetMapping("/profile")
	public ResponseEntity<User> findUserByJwtToken(@RequestHeader("Authorization") String jwt) throws Exception{
		User user = userService.findUserByJwtToken(jwt);
		return new ResponseEntity<>(user, HttpStatus.OK);
	}

	@PutMapping("/update-profile/{userId}")
	public ResponseEntity<?> updateUserProfile(@PathVariable Long userId, @RequestBody UserProfileRequest request) {
		Optional<User> optionalUser = userRepository.findById(userId);

		if (optionalUser.isPresent()) {
			User user = optionalUser.get();

			if (request.getFullName() != null && !request.getFullName().isEmpty()) {
				user.setFullName(request.getFullName());
			}

			if (request.getAvatar() != null && !request.getAvatar().isEmpty()) {
				user.setAvatar(request.getAvatar());
			}

			userRepository.save(user);

			return ResponseEntity.ok("Cập nhật thông tin thành công!");
		} else {
			return ResponseEntity.badRequest().body("Không tìm thấy người dùng!");
		}
	}

	@PostMapping("/change-password")
	public ResponseEntity<String> changePassword(
			@RequestHeader("Authorization") String authorizationHeader,
			@RequestBody ChangePasswordRequest request) {

		// Kiểm tra Authorization header có hợp lệ không
		if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Missing or invalid token.");
		}

		try {
			// Tìm user từ JWT (tái sử dụng logic của /profile)
			User user = userService.findUserByJwtToken(authorizationHeader);

			if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Mật khẩu hiện tại không chính xác!");
			}

			// Cập nhật mật khẩu mới (đã mã hóa)
			user.setPassword(passwordEncoder.encode(request.getNewPassword()));
			userRepository.save(user);

			return ResponseEntity.ok("Mật khẩu đã được thay đổi thành công!");

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Invalid token.");
		}
	}

}
