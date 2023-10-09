package com.cozyhome.onlineshop.userservice.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cozyhome.onlineshop.dto.auth.EmailRequest;
import com.cozyhome.onlineshop.dto.auth.LoginRequest;
import com.cozyhome.onlineshop.dto.auth.MessageResponse;
import com.cozyhome.onlineshop.dto.auth.SignupRequest;
import com.cozyhome.onlineshop.exception.AuthenticationException;
import com.cozyhome.onlineshop.userservice.model.User;
import com.cozyhome.onlineshop.userservice.security.JWT.JwtTokenUtil;
import com.cozyhome.onlineshop.userservice.security.service.SecurityService;
import com.cozyhome.onlineshop.userservice.security.service.SecurityTokenService;
import com.cozyhome.onlineshop.userservice.security.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@CrossOrigin({ "${api.front.base_url}", "${api.front.localhost}", "${api.front.test_url}",
		"${api.front.additional_url}", "${api.front.main.url}" })
@Tag(name = "Auth")
@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("${api.basePath}/auth")
public class AuthController {

	private final UserService userService;
	private final SecurityService securityService;
	private final JwtTokenUtil jwtTokenUtil;
	private final SecurityTokenService securityTokenService;

	private final String emailErrorMessage = "Error: Email is already in use!";
	private final String registrationSuccessMessage = "User registered successfully!";

	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
		String username = loginRequest.getUsername();
		boolean isAuthenticated = securityService.isAuthenticated(username, loginRequest.getPassword());
		if (isAuthenticated) {
			String token = jwtTokenUtil.generateToken(username);
			return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, token).build();
		} else {
			log.warn("[ON login]:: Authentication failed for user: {}", username);
			throw new AuthenticationException("Authentication failed for user");
		}
	}

	@PostMapping("/signup")
	public ResponseEntity<MessageResponse> registerUser(@RequestBody SignupRequest signUpRequest) {
		String email = signUpRequest.getEmail();
		if (userService.existsByEmail(email)) {
			return ResponseEntity.badRequest().body(new MessageResponse(emailErrorMessage));
		}
		User savedUser = userService.saveUser(signUpRequest);
		securityTokenService.createActivationUserToken(savedUser);
		return ResponseEntity.ok(new MessageResponse(registrationSuccessMessage));
	}

	@GetMapping("/activate")
	public ResponseEntity<MessageResponse> activateUser(@RequestParam String activationToken) {
		User activatedUser = userService.activateUser(activationToken);
		String token = jwtTokenUtil.generateToken(activatedUser.getEmail());
		return ResponseEntity.status(HttpStatus.OK).header(HttpHeaders.AUTHORIZATION, token).body(new MessageResponse("success"));
	}

	@GetMapping("/logout")
	public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (userDetails != null) {
			SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
			logoutHandler.logout(request, response, SecurityContextHolder.getContext().getAuthentication());
			return ResponseEntity.ok("Logout successful");
		} else {
			return ResponseEntity.ok("No user is logged in");
		}
	}

	@PostMapping("/login/forgot")
	public ResponseEntity<MessageResponse> forgetPassword(@RequestBody EmailRequest emailRequest, HttpServletRequest httpRequest) {
		securityTokenService.createPasswordResetToken(emailRequest.getEmail(), httpRequest.getRemoteAddr());
		return ResponseEntity.ok(new MessageResponse("success"));
	}

	@PostMapping("/login/reset")
	public ResponseEntity<MessageResponse> resetPassword(@RequestParam String resetPasswordToken, @RequestBody String newPassword) {
		User user = userService.resetPassword(resetPasswordToken, newPassword);
		String token = jwtTokenUtil.generateToken(user.getEmail());
		return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, token).build();
	}

}
