package com.cozyhome.onlineshop.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserInformationResponse {
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
}
