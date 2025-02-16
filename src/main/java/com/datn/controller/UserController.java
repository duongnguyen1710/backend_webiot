package com.datn.controller;

import com.datn.config.JwtProvider;
import com.datn.repository.UserRepository;
import com.datn.request.ChangePasswordRequest;
import com.datn.request.UserProfileRequest;
import com.datn.service.CloudinaryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.datn.entity.User;
import com.datn.service.UserService;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
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
	private CloudinaryService cloudinaryService;

	@Autowired
	private JwtProvider jwtProvider;

	@GetMapping("/profile")
	public ResponseEntity<?> findUserByJwtToken(@RequestHeader("Authorization") String jwt) throws Exception {
		User user = userService.findUserByJwtToken(jwt);

		// Kiểm tra tài khoản đã verify chưa
		if (!user.isVerified()) {
			return new ResponseEntity<>("Tài khoản chưa được xác minh", HttpStatus.FORBIDDEN);
		}

		return new ResponseEntity<>(user, HttpStatus.OK);
	}


	@PutMapping(value = "/update-profile/{userId}", consumes = {"multipart/form-data"})
	public ResponseEntity<?> updateUserProfile(
			@PathVariable Long userId,
			@RequestPart("request") String requestJson,
			@RequestPart(value = "avatar", required = false) MultipartFile avatarFile) {
		try {
			Optional<User> optionalUser = userRepository.findById(userId);
			if (!optionalUser.isPresent()) {
				return ResponseEntity.badRequest().body("Không tìm thấy người dùng!");
			}

			User user = optionalUser.get();

			// Chuyển đổi JSON thành đối tượng UserProfileRequest
			ObjectMapper objectMapper = new ObjectMapper();
			UserProfileRequest request = objectMapper.readValue(requestJson, UserProfileRequest.class);

			// ✅ Cập nhật tên nếu có
			if (request.getFullName() != null && !request.getFullName().isEmpty()) {
				user.setFullName(request.getFullName());
			}

			// ✅ Nếu có avatar mới, tải lên Cloudinary sử dụng `uploadImages()`
			if (avatarFile != null && !avatarFile.isEmpty()) {
				List<MultipartFile> imageList = new ArrayList<>();
				imageList.add(avatarFile);
				List<String> uploadedUrls = cloudinaryService.uploadImages(imageList);

				if (!uploadedUrls.isEmpty()) {
					user.setAvatar(uploadedUrls.get(0)); // Lấy ảnh đầu tiên
				}
			}

			userRepository.save(user);
			return ResponseEntity.ok("Cập nhật thông tin thành công!");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi cập nhật thông tin!");
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
