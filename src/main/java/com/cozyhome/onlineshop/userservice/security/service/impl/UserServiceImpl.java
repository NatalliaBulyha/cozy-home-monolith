package com.cozyhome.onlineshop.userservice.security.service.impl;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cozyhome.onlineshop.dto.auth.SignupRequest;
import com.cozyhome.onlineshop.userservice.model.Role;
import com.cozyhome.onlineshop.userservice.model.RoleE;
import com.cozyhome.onlineshop.userservice.model.User;
import com.cozyhome.onlineshop.userservice.model.UserStatusE;
import com.cozyhome.onlineshop.userservice.repository.RoleRepository;
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
	
	private final String admin = "admin";
	private final String manager = "manager";
	private final String roleErrorMessage = "Error: Role is not found.";

	@Override
	public void saveUser(SignupRequest signupRequest, String activationToken) {
		User user = User.builder()
				.email(signupRequest.getEmail())
				.password(encoder.encode(signupRequest.getPassword()))
				.firstName(signupRequest.getFirstName())
				.lastName(signupRequest.getLastName())
				.phoneNumber(signupRequest.getPhoneNumber())
				.activationToken(activationToken)
				.createdAt(LocalDateTime.now()).status(UserStatusE.ACTIVE)
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
		userRepository.save(user);
	}
	
	@Override
	public boolean existsByEmail(String email) {		
		return userRepository.existsByEmail(email);
	}

	@Override
	public User activateUser(String activationToken) {
		Optional<User> user = userRepository.getByActivationToken(activationToken);
		if(!user.isPresent()) {
			log.warn("[ON activateUser]:: user with activationToken::[ {} ] not found", activationToken);
            throw new IllegalArgumentException("User not found for activationToken - " + activationToken);
		}		
		log.info("[ON activateUser]:: loaded user with username [ {} ]", user.get().getEmail());
		return user.get();
	}

}
