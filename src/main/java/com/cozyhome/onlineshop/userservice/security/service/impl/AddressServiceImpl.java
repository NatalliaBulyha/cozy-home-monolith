package com.cozyhome.onlineshop.userservice.security.service.impl;

import com.cozyhome.onlineshop.dto.user.AddressIdDto;
import com.cozyhome.onlineshop.dto.user.AddressRequest;
import com.cozyhome.onlineshop.dto.user.AddressResponse;
import com.cozyhome.onlineshop.exception.DataAlreadyExistException;
import com.cozyhome.onlineshop.exception.DataNotFoundException;
import com.cozyhome.onlineshop.userservice.model.Address;
import com.cozyhome.onlineshop.userservice.model.User;
import com.cozyhome.onlineshop.userservice.repository.AddressRepository;
import com.cozyhome.onlineshop.userservice.repository.UserRepository;
import com.cozyhome.onlineshop.userservice.security.service.AddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    @Override
    public AddressResponse saveAddress(AddressRequest addressRequest, String userId) {
        List<Address> addresses = new ArrayList<>();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException(String.format("User with id = %s not found.", userId)));

        Address address = Address.builder()
                .city(addressRequest.getCity())
                .street(addressRequest.getStreet())
                .house(addressRequest.getHouse())
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();

        if (!addressRequest.getApartment().isEmpty()) {
            address.setApartment(Integer.parseInt(addressRequest.getApartment()));
        }
        if (!addressRequest.getEntrance().isEmpty()) {
            address.setEntrance(Integer.parseInt(addressRequest.getEntrance()));
        }
        if (!addressRequest.getFloor().isEmpty()) {
            address.setFloor(Integer.parseInt(addressRequest.getFloor()));
        }
        if (!addressRequest.getWithLift().isEmpty()) {
            address.setWithLift(Boolean.parseBoolean(addressRequest.getWithLift()));
        }
        if (user.getAddresses() != null) {
            if (user.getAddresses().contains(address)) {
                throw new DataAlreadyExistException(String.format("User with id %s already has this address: %s",
                        userId, address));
            } else {
                addresses = user.getAddresses();
            }
        }
        Address savedAddress = addressRepository.save(address);
        log.info("[ON saveAddress] :: address with id {} was saved.", savedAddress.getId());
        addresses.add(savedAddress);
        user.setAddresses(addresses);
        userRepository.save(user);
        log.info("[ON saveAddress] :: user with id {} saved address with id {} to his address list.", userId, savedAddress.getId());
        return modelMapper.map(savedAddress, AddressResponse.class);
    }

    @Override
    public void deleteAddress(AddressIdDto addressId, String userId) {
        Address addressFromDb = addressRepository.findById(addressId.getId())
                .orElseThrow(() -> new DataNotFoundException(String.format("Address with id = %s not found.", addressId.getId())));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException(String.format("User with id = %s not found.", userId)));


        if(user.getAddresses().contains(addressFromDb)) {
            user.getAddresses().remove(addressFromDb);
            userRepository.save(user);
            log.info("[ON deleteAddress] :: address with id {} was deleted from user list (user id = {}).",
                    addressId.getId(), userId);
        }
        addressRepository.delete(addressFromDb);
        log.info("[ON deleteAddress] :: address with id {} was deleted.", addressId.getId());
    }
}
