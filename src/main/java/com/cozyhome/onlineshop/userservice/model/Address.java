package com.cozyhome.onlineshop.userservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "Address")
public class Address {

    @Id
    @EqualsAndHashCode.Exclude
    private String id;
    private String city;
    private String street;
    private String house;
    private Integer apartment;
    private Integer comment;
    @EqualsAndHashCode.Exclude
    private LocalDateTime createdAt;
    @EqualsAndHashCode.Exclude
    private LocalDateTime modifiedAt;
    @DBRef
    private User user;
}
