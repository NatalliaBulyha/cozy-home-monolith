package com.cozyhome.onlineshop.userservice.security.service;

import com.cozyhome.onlineshop.dto.auth.NewPasswordRequest;
import com.cozyhome.onlineshop.dto.auth.SignupRequest;
import com.cozyhome.onlineshop.dto.user.UserInformationRequest;
import com.cozyhome.onlineshop.dto.user.UserInformationResponse;
import com.cozyhome.onlineshop.userservice.model.User;

public interface UserService {
	
	User saveUser(SignupRequest signupRequest);

	boolean existsByEmail(String email);
	
	User activateUser(String activationToken);
	
	User resetPassword(String token, NewPasswordRequest newPassword);

	UserInformationResponse updateUserData(UserInformationRequest userInformationDto, String userId);
	
}
