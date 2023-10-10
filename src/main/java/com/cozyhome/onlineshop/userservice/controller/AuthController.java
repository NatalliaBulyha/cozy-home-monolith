package com.cozyhome.onlineshop.userservice.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.validation.annotation.Validated;
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
import com.cozyhome.onlineshop.dto.auth.NewPasswordRequest;
import com.cozyhome.onlineshop.dto.auth.SignupRequest;
import com.cozyhome.onlineshop.exception.AuthenticationException;
import com.cozyhome.onlineshop.productservice.controller.swagger.SwaggerResponse;
import com.cozyhome.onlineshop.userservice.model.User;
import com.cozyhome.onlineshop.userservice.security.JWT.JwtTokenUtil;
import com.cozyhome.onlineshop.userservice.security.service.SecurityService;
import com.cozyhome.onlineshop.userservice.security.service.SecurityTokenService;
import com.cozyhome.onlineshop.userservice.security.service.UserService;
import com.cozyhome.onlineshop.validation.ValidUUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@CrossOrigin({ "${api.front.base_url}", "${api.front.localhost}", "${api.front.test_url}",
		"${api.front.additional_url}", "${api.front.main.url}" })
@Tag(name = "Auth")
@RequiredArgsConstructor
@Slf4j
@Validated
@RestController
@RequestMapping("${api.basePath}/auth")
public class AuthController {

	private final UserService userService;
	private final SecurityService securityService;
	private final JwtTokenUtil jwtTokenUtil;
	private final SecurityTokenService securityTokenService;

	private final String emailErrorMessage = "Error: Email is already in use!";

	@Operation(summary = "User Login", description = "Allows an existing user to log in using their email and password.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = SwaggerResponse.Code.CODE_200, description = SwaggerResponse.Message.CODE_200_FOUND_DESCRIPTION) })
	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody @Valid LoginRequest loginRequest) {
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

	@Operation(summary = "User Registration", description = "Registers a new user and sends an email with a link to activate their account.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = SwaggerResponse.Code.CODE_200, description = SwaggerResponse.Message.CODE_200_FOUND_DESCRIPTION) })
	@PostMapping("/signup")
	public ResponseEntity<MessageResponse> registerUser(@RequestBody @Valid SignupRequest signUpRequest) {
		String email = signUpRequest.getEmail();
		if (userService.existsByEmail(email)) {
			return ResponseEntity.badRequest().body(new MessageResponse(emailErrorMessage));
		}
		userService.saveUser(signUpRequest);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@Operation(summary = "Activate e-mail.", description = "User follows the link sent to him during registration and activates his mail in this method")
	@ApiResponses(value = {
			@ApiResponse(responseCode = SwaggerResponse.Code.CODE_200, description = SwaggerResponse.Message.CODE_200_FOUND_DESCRIPTION) })
	@GetMapping("/activate")
	public ResponseEntity<MessageResponse> activateUser(@RequestParam @ValidUUID String activationToken) {
		User activatedUser = userService.activateUser(activationToken);
		String token = jwtTokenUtil.generateToken(activatedUser.getEmail());
		return ResponseEntity.status(HttpStatus.OK).header(HttpHeaders.AUTHORIZATION, token)
				.body(new MessageResponse("success"));
	}

	@Operation(summary = "Logout.", description = "Logout.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = SwaggerResponse.Code.CODE_200, description = SwaggerResponse.Message.CODE_200_FOUND_DESCRIPTION) })
	@GetMapping("/logout")
	public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (userDetails != null) {
			response.setHeader("Authorization", null);
			SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
			logoutHandler.logout(request, response, SecurityContextHolder.getContext().getAuthentication());
			return ResponseEntity.status(HttpStatus.OK).build();
		} else {
			return ResponseEntity.status(HttpStatus.OK).build();
		}
	}

	@Operation(summary = "Send a password reset link to the user.", description = "Sends a password reset link to the user's email address if they have forgotten their password.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = SwaggerResponse.Code.CODE_200, description = SwaggerResponse.Message.CODE_200_FOUND_DESCRIPTION) })
	@PostMapping("/login/forgot")
	public ResponseEntity<MessageResponse> forgetPassword(@RequestBody @Valid EmailRequest emailRequest,
			HttpServletRequest httpRequest) {
		securityTokenService.createPasswordResetToken(emailRequest.getEmail(), httpRequest.getRemoteAddr());
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@Operation(summary = "Reset User Password", description = "User follows the link previously sent to his e-mail and enters a new password.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = SwaggerResponse.Code.CODE_200, description = SwaggerResponse.Message.CODE_200_FOUND_DESCRIPTION) })
	@PostMapping("/login/reset")
	public ResponseEntity<MessageResponse> resetPassword(@RequestParam @ValidUUID String resetPasswordToken,
			@RequestBody @Valid NewPasswordRequest newPassword) {
		User user = userService.resetPassword(resetPasswordToken, newPassword);
		String token = jwtTokenUtil.generateToken(user.getEmail());
		return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, token).build();
	}

}
