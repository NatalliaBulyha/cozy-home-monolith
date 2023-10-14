package com.cozyhome.onlineshop.dto.user;

import com.cozyhome.onlineshop.validation.ValidPassword;

import jakarta.validation.constraints.AssertTrue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
//@ValidPasswordsMatch
public class PasswordChangeRequest {

	@ValidPassword
	private String oldPassword;

	@ValidPassword
	private String newPassword;

	@ValidPassword
	private String repeatedNewPassword;
	
	@AssertTrue(message = "New passwords do not match.")
    public boolean isNewPasswordsMatch() {
        return newPassword != null && newPassword.equals(repeatedNewPassword);
    }

}
