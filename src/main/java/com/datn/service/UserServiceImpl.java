package com.datn.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.datn.config.JwtProvider;
import com.datn.entity.User;
import com.datn.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private JwtProvider jwtProvider;
	@Override
	public User findUserByJwtToken(String jwt) throws Exception {
		String email =jwtProvider.getEmailFromJwtToken(jwt);
		User user = findUserByEmail(email);
		return user;
	}

	@Override
	public User findUserByEmail(String email) throws Exception {
		User user = userRepository.findByEmail(email);
		
		if(user==null) {
			throw new Exception("Không tìm thấy người dùng");
		}
		return user;
	}

	@Override
	public User save(User user) {
		return userRepository.save(user);
	}
	
}
