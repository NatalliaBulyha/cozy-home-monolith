package com.cozyhome.onlineshop.dto.auth;

import java.util.Set;

import com.cozyhome.onlineshop.validation.ValidEmail;
import com.cozyhome.onlineshop.validation.ValidOptionalFieldBirthday;
import com.cozyhome.onlineshop.validation.ValidPassword;
import com.cozyhome.onlineshop.validation.ValidPhoneNumber;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@ValidOptionalFieldBirthday
public class SignupRequest {

	@ValidEmail
	private String email;
	@ValidPassword
	private String password;
	@NotBlank
	@Min(value = 2)
	@Max(value = 32)
	private String firstName;
	private String birthday;
	@NotBlank
	@Min(value = 2)
	@Max(value = 32)
	private String lastName;
	@ValidPhoneNumber
	private String phoneNumber;
	private Set<String> roles;

}
