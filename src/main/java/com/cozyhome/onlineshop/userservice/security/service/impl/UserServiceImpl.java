package com.cozyhome.onlineshop.userservice.security.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.cozyhome.onlineshop.dto.auth.NewPasswordRequest;
import com.cozyhome.onlineshop.dto.user.UserInformationRequest;
import com.cozyhome.onlineshop.dto.user.UserInformationResponse;
import com.cozyhome.onlineshop.exception.AuthenticationException;
import com.cozyhome.onlineshop.exception.DataAlreadyExistException;
import com.cozyhome.onlineshop.exception.DataNotFoundException;
import com.cozyhome.onlineshop.userservice.security.service.SecurityTokenService;
import com.cozyhome.onlineshop.userservice.security.service.builder.UserBuilder;
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
	private final SecurityTokenService securityTokenService;
	private final UserBuilder userBuilder;

	private final String admin = "admin";
	private final String manager = "manager";
	private final String roleErrorMessage = "Error: Role is not found.";

	@Override
	public void saveUser(SignupRequest signupRequest) {
		User user = User.builder()
				.email(signupRequest.getEmail())
				.password(encoder.encode(signupRequest.getPassword()))
				.firstName(signupRequest.getFirstName())
				.lastName(signupRequest.getLastName())
				.phoneNumber(signupRequest.getPhoneNumber())
				.createdAt(LocalDateTime.now())
				.status(UserStatusE.ACTIVE)
				.build();

		if (signupRequest.getBirthday() != null && !signupRequest.getBirthday().isEmpty()) {
			LocalDate birthday = LocalDate.parse(signupRequest.getBirthday());
			user.setBirthday(birthday);
		}

		Set<String> userRoles = signupRequest.getRoles();
		Set<Role> roles = new HashSet<>();

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
		securityTokenService.createActivationUserToken(savedUser);
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
	public User resetPassword(String token, NewPasswordRequest newPassword) {
		PasswordResetToken resetToken = (PasswordResetToken) securityTokenRepository.findByToken(token);

        if (resetToken == null || resetToken.isExpired()) {
            throw new IllegalArgumentException("Invalid or expired token");
        }

        User user = resetToken.getUser();
        user.setPassword(encoder.encode(newPassword.getPassword()));
        userRepository.save(user);

        securityTokenRepository.delete(resetToken);	
        return user;
	}

	@Override
	public UserInformationResponse updateUserData(UserInformationRequest userInformationDto, String userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new DataNotFoundException(
						String.format("User with email = %s not found.", userInformationDto.getEmail())));

		if (!user.getLastName().equals(userInformationDto.getLastName())) {
			user.setLastName(userInformationDto.getLastName());
		}
		if (!user.getFirstName().equals(userInformationDto.getFirstName())) {
			user.setFirstName(userInformationDto.getFirstName());
		}
		if (!user.getPhoneNumber().equals(userInformationDto.getPhoneNumber())) {
			user.setPhoneNumber(userInformationDto.getPhoneNumber());
		}
		if (!user.getEmail().equals(userInformationDto.getEmail())) {
			if (userRepository.existsByEmail(userInformationDto.getEmail()) ) {
				throw new DataAlreadyExistException(String.format("Email is already in use by another user. " +
						"Forbidden to change mail to %s.", userInformationDto.getEmail()));
			}
			user.setEmail(userInformationDto.getEmail());
		}
		if (!userInformationDto.getBirthday().isEmpty()) {
			LocalDate birthday = LocalDate.parse(userInformationDto.getBirthday());
			user.setBirthday(birthday);
		}
		if (userInformationDto.getOldPassword() != null && !userInformationDto.getOldPassword().isEmpty()) {
			if (encoder.matches(userInformationDto.getOldPassword(), user.getPassword())) {
				user.setPassword(encoder.encode(userInformationDto.getNewPassword()));
			} else {
				throw new AuthenticationException("Wrong old password entered.");
			}
		}

		User updatedUser = userRepository.save(user);
		return userBuilder.buildUserInformationResponse(updatedUser);
	}

	@Override
	public UserInformationResponse getUserInfo(String userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new DataNotFoundException(String.format("User with id = %s not found.", userId)));
		return userBuilder.buildUserInformationResponse(user);
	}

}
