package com.cozyhome.onlineshop.userservice.security.service.impl;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.cozyhome.onlineshop.dto.user.UserInformationDto;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cozyhome.onlineshop.dto.auth.SignupRequest;
import com.cozyhome.onlineshop.userservice.model.Role;
import com.cozyhome.onlineshop.userservice.model.RoleE;
import com.cozyhome.onlineshop.userservice.model.User;
import com.cozyhome.onlineshop.userservice.model.UserStatusE;
import com.cozyhome.onlineshop.userservice.model.token.PasswordResetToken;
import com.cozyhome.onlineshop.userservice.model.token.SecurityToken;
import com.cozyhome.onlineshop.userservice.repository.RoleRepository;
import com.cozyhome.onlineshop.userservice.repository.SecurityTokenRepository;
import com.cozyhome.onlineshop.userservice.repository.UserRepository;
import com.cozyhome.onlineshop.userservice.security.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder encoder;
	private final SecurityTokenRepository securityTokenRepository;

	private final String admin = "admin";
	private final String manager = "manager";
	private final String roleErrorMessage = "Error: Role is not found.";

	@Override
	public User saveUser(SignupRequest signupRequest) {
		User user = User.builder().email(signupRequest.getEmail()).password(encoder.encode(signupRequest.getPassword()))
				.firstName(signupRequest.getFirstName()).lastName(signupRequest.getLastName())
				.phoneNumber(signupRequest.getPhoneNumber()).createdAt(LocalDateTime.now()).status(UserStatusE.ACTIVE)
				.build();

		Set<String> userRoles = signupRequest.getRoles();
		Set<Role> roles = new HashSet<Role>();

		if (userRoles == null) {
			Role userRole = roleRepository.getByName(RoleE.ROLE_CUSTOMER)
					.orElseThrow(() -> new RuntimeException(roleErrorMessage));
			roles.add(userRole);
		} else {
			userRoles.forEach(role -> {
				switch (role) {
				case admin:
					Role adminRole = roleRepository.getByName(RoleE.ROLE_ADMIN)
							.orElseThrow(() -> new RuntimeException(roleErrorMessage));
					roles.add(adminRole);
					break;
				case manager:
					Role managerRole = roleRepository.getByName(RoleE.ROLE_MANAGER)
							.orElseThrow(() -> new RuntimeException(roleErrorMessage));
					roles.add(managerRole);
					break;
				default:
					Role userRole = roleRepository.getByName(RoleE.ROLE_CUSTOMER)
							.orElseThrow(() -> new RuntimeException(roleErrorMessage));
					roles.add(userRole);
				}
			});
		}
		user.setRoles(roles);
		User savedUser = userRepository.save(user);
		return savedUser;
	}

	@Override
	public boolean existsByEmail(String email) {
		return userRepository.existsByEmail(email);
	}

	@Override
	public User activateUser(String token) {
		SecurityToken activationToken = securityTokenRepository.findByToken(token);

		if (activationToken == null || activationToken.isExpired()) {
			throw new IllegalArgumentException("Invalid or expired activation token");
		}

		User user = activationToken.getUser();
		user.setActivated(true);
		userRepository.save(user);

		securityTokenRepository.delete(activationToken);

		return user;
	}

	@Override
	public User resetPassword(String token, String newPassword) {
		PasswordResetToken resetToken = (PasswordResetToken) securityTokenRepository.findByToken(token);

        if (resetToken == null || resetToken.isExpired()) {
            throw new IllegalArgumentException("Invalid or expired token");
        }

        User user = resetToken.getUser();
        user.setPassword(encoder.encode(newPassword));
        userRepository.save(user);

        securityTokenRepository.delete(resetToken);	
        return user;
	}

	@Override
	public void updateUserData(UserInformationDto userInformationDto) {

	}

}
