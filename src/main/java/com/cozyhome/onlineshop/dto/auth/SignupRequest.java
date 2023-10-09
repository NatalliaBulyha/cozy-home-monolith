package com.cozyhome.onlineshop.dto.auth;

import java.util.Set;

import com.cozyhome.onlineshop.validation.ValidEmail;
import com.cozyhome.onlineshop.validation.ValidPassword;
import com.cozyhome.onlineshop.validation.ValidPhoneNumber;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SignupRequest {

	@ValidEmail
	private String email;
	@ValidPassword
	private String password;
	@NotBlank
	private String firstName;
	@NotBlank
	private String lastName;
	@ValidPhoneNumber
	private String phoneNumber;
	private Set<String> roles;

}
