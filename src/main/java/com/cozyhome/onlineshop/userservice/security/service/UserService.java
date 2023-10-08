package com.cozyhome.onlineshop.userservice.security.service;

import com.cozyhome.onlineshop.dto.auth.SignupRequest;
import com.cozyhome.onlineshop.userservice.model.User;

public interface UserService {
	
	void saveUser(SignupRequest signuRequest, String activationToken);

	boolean existsByEmail(String email);
	
	User activateUser(String activationToken);
}
