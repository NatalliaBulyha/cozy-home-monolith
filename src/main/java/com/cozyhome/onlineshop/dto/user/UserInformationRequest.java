package com.cozyhome.onlineshop.dto.user;

import com.cozyhome.onlineshop.validation.ValidFirstNameAndLastName;
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
public class UserInformationRequest {
    @ValidEmail
    private String email;
    private String oldPassword;
    private String newPassword;
    @ValidOptionalFieldBirthday
    private String birthday;
    @ValidFirstNameAndLastName(message = "Invalid firstName. FirstName must be not null, greater than " +
            "or equal to 2 and less than or equal to 32, letters only.")
    private String firstName;
    @ValidFirstNameAndLastName(message = "Invalid lastName. LastName must be not null, greater than " +
            "or equal to 2 and less than or equal to 32, letters only.")
    private String lastName;
    @ValidPhoneNumber
    private String phoneNumber;
}
