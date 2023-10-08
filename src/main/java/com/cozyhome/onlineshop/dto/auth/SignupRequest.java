package com.cozyhome.onlineshop.dto.auth;

import java.util.Set;

import com.cozyhome.onlineshop.validation.ValidEmail;
import com.cozyhome.onlineshop.validation.ValidPassword;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
	@Pattern(regexp = "\\+38 \\(\\d{3}\\) \\d{3} - \\d{2} - \\d{2}",
			message = "Invalid phone number. Phone number must be +38 (***) *** - ** - **.")
	private String phoneNumber;
	private Set<String> roles;

}
