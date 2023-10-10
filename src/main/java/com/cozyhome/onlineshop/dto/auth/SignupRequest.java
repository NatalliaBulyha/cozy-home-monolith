package com.cozyhome.onlineshop.dto.auth;

import java.util.Set;

import com.cozyhome.onlineshop.validation.ValidEmail;
import com.cozyhome.onlineshop.validation.ValidOptionalFieldBirthday;
import com.cozyhome.onlineshop.validation.ValidPassword;
import com.cozyhome.onlineshop.validation.ValidPhoneNumber;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
	@NotNull(message = "FirstName must not be null.")
	@Size(min = 2, max = 32, message = "FirstName must be greater than or equal to 2 and less than or equal to 32")
	private String firstName;
	@NotNull(message = "LastName must not be null.")
	@Size(min = 2, max = 32, message = "LastName must be greater than or equal to 2 and less than or equal to 32")
	private String lastName;
	@ValidOptionalFieldBirthday
	private String birthday;
	@ValidPhoneNumber
	private String phoneNumber;
	private Set<String> roles;

}
