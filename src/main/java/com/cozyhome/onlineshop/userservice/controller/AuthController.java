package com.cozyhome.onlineshop.userservice.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cozyhome.onlineshop.dto.auth.LoginRequest;
import com.cozyhome.onlineshop.dto.auth.MessageResponse;
import com.cozyhome.onlineshop.dto.auth.SignupRequest;
import com.cozyhome.onlineshop.emailservice.Emailservice;
import com.cozyhome.onlineshop.exception.AuthenticationException;
import com.cozyhome.onlineshop.userservice.model.User;
import com.cozyhome.onlineshop.userservice.security.JWT.JwtTokenUtil;
import com.cozyhome.onlineshop.userservice.security.service.SecurityService;
import com.cozyhome.onlineshop.userservice.security.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("${api.basePath}/auth")
public class AuthController {

	private final UserService userService;
	private final SecurityService securityService;
	private final JwtTokenUtil jwtTokenUtil;
	private final Emailservice emailService;

	private final String emailErrorMessage = "Error: Email is already in use!";
	private final String registrationSuccessMessage = "User registered successfully!";
	
	@Value("${activation.link}")
	private String link;

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
		if (userService.existsByEmail(signUpRequest.getEmail())) {
			return ResponseEntity.badRequest().body(new MessageResponse(emailErrorMessage));
		}		
		final String activationToken = generateActivationToken();		
		userService.saveUser(signUpRequest, activationToken);	
		
		String activationLink = link + activationToken;
        emailService.sendActivationEmail(signUpRequest.getEmail(), activationLink);

		return ResponseEntity.ok(new MessageResponse(registrationSuccessMessage));
	}

	@GetMapping("/activate")
	public ResponseEntity<MessageResponse> activate(@RequestParam String activationToken) {
		User user = userService.activateUser(activationToken);
		String token = jwtTokenUtil.generateToken(user.getEmail());
		return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, token).build();
	}

	private String generateActivationToken() {
		return UUID.randomUUID().toString();
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

}
