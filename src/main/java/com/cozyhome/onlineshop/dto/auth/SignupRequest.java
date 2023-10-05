package com.cozyhome.onlineshop.dto.auth;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SignupRequest {
	
	private String email;
	private String password;
	private String firstName;
	private String lastName;
	private String phoneNumber;
	private Set<String> roles;

}
