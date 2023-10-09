package com.cozyhome.onlineshop.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserInformationDto {
    private String email;
    private String oldPassword;
    private String newPassword;
    private String firstName;
    private String lastName;
    private String phoneNumber;
}
