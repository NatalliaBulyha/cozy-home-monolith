package com.cozyhome.onlineshop.userservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "Address")
public class Address {

    @Id
    private String id;
    private String city;
    private String street;
    private String house;
    private Integer apartment;
    private Integer entrance;
    private Integer floor;
    private boolean withLift;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
