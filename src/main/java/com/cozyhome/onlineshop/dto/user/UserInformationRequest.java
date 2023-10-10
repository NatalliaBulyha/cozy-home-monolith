package com.cozyhome.onlineshop.dto.user;

import com.cozyhome.onlineshop.validation.ValidOptionalFieldBirthday;
import com.cozyhome.onlineshop.validation.ValidOptionalFieldsPassword;
import com.cozyhome.onlineshop.validation.ValidEmail;
import com.cozyhome.onlineshop.validation.ValidPhoneNumber;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@ValidOptionalFieldsPassword
@ValidOptionalFieldBirthday
public class UserInformationRequest {
    @ValidEmail
    private String email;
    private String oldPassword;
    private String newPassword;
    private String birthday;
    private String firstName;
    private String lastName;
    @ValidPhoneNumber
    private String phoneNumber;
}
