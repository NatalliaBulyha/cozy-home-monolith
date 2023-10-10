package com.cozyhome.onlineshop.dto.user;

import com.cozyhome.onlineshop.validation.ValidOptionalFieldBirthday;
import com.cozyhome.onlineshop.validation.ValidOptionalFieldsPassword;
import com.cozyhome.onlineshop.validation.ValidEmail;
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
@ValidOptionalFieldsPassword
public class UserInformationRequest {
    @ValidEmail
    private String email;
    private String oldPassword;
    private String newPassword;
    @ValidOptionalFieldBirthday
    private String birthday;
    @NotNull(message = "FirstName must not be null.")
    @Size(min = 2, max = 32, message = "FirstName must be greater than or equal to 2 and less than or equal to 32")
    private String firstName;
    @NotNull(message = "LastName must not be null.")
    @Size(min = 2, max = 32, message = "LastName must be greater than or equal to 2 and less than or equal to 32")
    private String lastName;
    @ValidPhoneNumber
    private String phoneNumber;
}
