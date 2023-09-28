package com.cozyhome.onlineshop.userservice.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.UniqueElements;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Document(collection = "User-Auth")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    private String firstName;
    private String lastName;
    @UniqueElements
    private String email;
    private String password;
    @UniqueElements
    private String phoneNumber;

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    private UserStatus status;
    @DBRef
    private Role role;

    public enum UserStatus{
        ACTIVE, BLOCKED, DELETED
    }

}
