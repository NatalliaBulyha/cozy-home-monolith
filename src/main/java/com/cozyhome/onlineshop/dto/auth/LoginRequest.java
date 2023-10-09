package com.cozyhome.onlineshop.dto.auth;

import com.cozyhome.onlineshop.validation.ValidEmail;
import com.cozyhome.onlineshop.validation.ValidPassword;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class LoginRequest {

	@ValidEmail
	private String username;
	@ValidPassword
	@NotBlank
	private String password;
}
