package com.cozyhome.onlineshop.userservice.controller;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.cozyhome.onlineshop.dto.auth.LoginRequest;
import com.cozyhome.onlineshop.dto.auth.MessageResponse;
import com.cozyhome.onlineshop.dto.auth.SignupRequest;
import com.cozyhome.onlineshop.userservice.model.Role;
import com.cozyhome.onlineshop.userservice.model.RoleE;
import com.cozyhome.onlineshop.userservice.model.User;
import com.cozyhome.onlineshop.userservice.model.UserStatusE;
import com.cozyhome.onlineshop.userservice.repository.RoleRepository;
import com.cozyhome.onlineshop.userservice.repository.UserRepository;
import com.cozyhome.onlineshop.userservice.security.SecurityService;
import com.cozyhome.onlineshop.userservice.security.JWT.JwtTokenUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@RestController
public class AuthController {

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final SecurityService securityService;
	private final PasswordEncoder encoder;
	private final JwtTokenUtil jwtTokenUtil;

	@PostMapping("${api.basePath}/auth/login")
	public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
		String username = loginRequest.getUsername();
		try {
			String token = null;
			boolean isAuthenticated = securityService.isAuthenticated(username, loginRequest.getPassword());
			if (isAuthenticated) {
				token = jwtTokenUtil.generateToken(username);
			}
			log.info("[ON login]:: setting header and token to ResponseEntity");
			return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, token).body("Welcome");
		} catch (BadCredentialsException e) {
			log.error("[ON login]:: {}", e.getLocalizedMessage());
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}
	
	@GetMapping("/api/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok()
                .body("Welcome, admin");
    }
	
    @GetMapping("${api.basePath}/auth/expired-jwt")
    public ResponseEntity<String> showTokenExpired(HttpServletRequest request, HttpServletResponse response) {
        return ResponseEntity.status(response.getStatus())
                .body("JWT Token expired.");
    }
	
	@PostMapping("${api.basePath}/auth/signup")
	  public ResponseEntity<?> registerUser(@RequestBody SignupRequest signUpRequest) {
		final String emailErrorMessage = "Error: Email is already in use!";
		final String roleErrorMessage = "Error: Role is not found.";
		final String registrationSuccessMessage = "User registered successfully!";
		final String admin = "admin";
		final String manager = "manager";
	    if (userRepository.existsByEmail(signUpRequest.getEmail())) {
	      return ResponseEntity
	          .badRequest()
	          .body(new MessageResponse(emailErrorMessage));
	    }

	    User user = User.builder()
	    .email(signUpRequest.getEmail())
	    .password(encoder.encode(signUpRequest.getPassword()))
	    .firstName(signUpRequest.getFirstName())
	    .lastName(signUpRequest.getLastName())
	    .phoneNumber(signUpRequest.getPhoneNumber())
	    .createdAt(LocalDateTime.now())
	    .status(UserStatusE.ACTIVE).build();

	    Set<String> userRoles = signUpRequest.getRoles();
	    Set<Role> roles = new HashSet<Role>();

	    if (userRoles == null) {
	      Role userRole = roleRepository.getByName(RoleE.CUSTOMER)
	          .orElseThrow(() -> new RuntimeException(roleErrorMessage));
	      roles.add(userRole);
	    } else {
	    	userRoles.forEach(role -> {
	        switch (role) {
	        case admin:
	          Role adminRole = roleRepository.getByName(RoleE.ADMIN)
	              .orElseThrow(() -> new RuntimeException(roleErrorMessage));
	          roles.add(adminRole);
	          break;
	        case manager:
	          Role managerRole = roleRepository.getByName(RoleE.MANAGER)
	              .orElseThrow(() -> new RuntimeException(roleErrorMessage));
	          roles.add(managerRole);
	          break;
	        default:
	          Role userRole = roleRepository.getByName(RoleE.CUSTOMER)
	              .orElseThrow(() -> new RuntimeException(roleErrorMessage));
	          roles.add(userRole);
	        }
	      });
	    }

	    user.setRoles(roles);
	    userRepository.save(user);

	    return ResponseEntity.ok(new MessageResponse(registrationSuccessMessage));
	  }
	
	@GetMapping("${api.basePath}/auth/logout")
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
