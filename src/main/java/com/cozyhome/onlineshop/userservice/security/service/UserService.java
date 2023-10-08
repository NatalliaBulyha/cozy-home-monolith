package com.cozyhome.onlineshop.userservice.security.service;

import com.cozyhome.onlineshop.dto.auth.SignupRequest;
import com.cozyhome.onlineshop.userservice.model.User;

public interface UserService {
	
	User saveUser(SignupRequest signuRequest);

	boolean existsByEmail(String email);
	
	User activateUser(String activationToken);
	
	User resetPassword(String token, String newPassword);
	
}
