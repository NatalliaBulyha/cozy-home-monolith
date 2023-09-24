package com.cozyhome.onlineshop.userservice.model;

import java.time.LocalDateTime;

import org.hibernate.validator.constraints.UniqueElements;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString(exclude = { "password" })
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "User")
public class User {

	@Id
	private String id;

	@NotBlank(message = "Username may not be blank")
	@Size(max = 30)
	@UniqueElements
	private String username;

	@DBRef
	private Role role;

	@NotBlank(message = "First name may not be blank")
	@Size(max = 30)
	private String firstName;

	@NotBlank(message = "Last name may not be blank")
	@Size(max = 30)
	private String lastName;
	
	@UniqueElements
	private String email;
	
	@UniqueElements
	private String phoneNumber;

	@JsonIgnore
	@Size(max = 30)
	private String password;
	
	private LocalDateTime createdAt;
	private LocalDateTime modifiedAt;
	
	private UserStatus status;
	
	public enum UserStatus{
		ACTIVE,
		BLOCKED,
		DELETED;
	}
	
}
