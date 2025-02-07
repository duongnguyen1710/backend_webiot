package com.datn.controller;

import com.datn.repository.UserRepository;
import com.datn.request.UserProfileRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
}
