package com.cozyhome.onlineshop.userservice.security;

import com.cozyhome.onlineshop.dto.auth.SignupRequest;

public interface UserService {
	
	void saveUser(SignupRequest signuRequest);

	boolean existsByEmail(String email);
}
