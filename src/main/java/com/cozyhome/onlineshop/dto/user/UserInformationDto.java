package com.cozyhome.onlineshop.dto.user;

import com.cozyhome.onlineshop.validation.ValidEmail;
import com.cozyhome.onlineshop.validation.ValidPassword;
import com.cozyhome.onlineshop.validation.ValidPhoneNumber;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserInformationDto {
    @ValidEmail
    private String email;
    @ValidPassword
    private String oldPassword;
    @ValidPassword
    private String newPassword;
    private String firstName;
    private String lastName;
    @ValidPhoneNumber
    private String phoneNumber;
}
