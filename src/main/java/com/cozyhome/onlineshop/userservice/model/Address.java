package com.cozyhome.onlineshop.userservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@ToString
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return withLift == address.withLift &&
                Objects.equals(city, address.city) &&
                Objects.equals(street, address.street) &&
                Objects.equals(house, address.house) &&
                Objects.equals(apartment, address.apartment) &&
                Objects.equals(entrance, address.entrance) &&
                Objects.equals(floor, address.floor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(city, street, house, apartment, entrance, floor, withLift);
    }
}
