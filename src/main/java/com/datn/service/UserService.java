package com.datn.service;

import com.datn.entity.User;

public interface UserService {
	public User findUserByJwtToken(String jwt) throws Exception;
	
	public User findUserByEmail(String email) throws Exception;
	
	 public User save(User user);
}
