package com.cozyhome.onlineshop.userservice.security.service.impl;

import com.cozyhome.onlineshop.dto.user.AddressIdDto;
import com.cozyhome.onlineshop.dto.user.AddressRequest;
import com.cozyhome.onlineshop.dto.user.AddressResponse;
import com.cozyhome.onlineshop.exception.DataNotFoundException;
import com.cozyhome.onlineshop.userservice.model.Address;
import com.cozyhome.onlineshop.userservice.model.User;
import com.cozyhome.onlineshop.userservice.repository.AddressRepositoryCustom;
import com.cozyhome.onlineshop.userservice.repository.AddressRepository;
import com.cozyhome.onlineshop.userservice.repository.UserRepository;
import com.cozyhome.onlineshop.userservice.security.service.AddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final AddressRepositoryCustom addressCustomRepository;
    @Override
    public AddressResponse saveAddress(AddressRequest addressRequest, String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException(String.format("User with id = %s not found.", userId)));

        Address address = modelMapper.map(addressRequest, Address.class);
        address.setCreatedAt(LocalDateTime.now());
        address.setModifiedAt(LocalDateTime.now());
        address.setUser(user);

        Optional<Address> existingAddress = addressCustomRepository.findAddressByFields(address);
        if (existingAddress.isPresent()) {
            log.info("[ON saveAddress] :: this address already exists for the user with id {}. The user will receive " +
                    "information about an existing address with id {}", user.getId(), existingAddress.get().getId());
            return modelMapper.map(existingAddress, AddressResponse.class);
        } else {
            Address savedAddress = addressRepository.save(address);
            log.info("[ON saveAddress] :: address with id {} was saved.", savedAddress.getId());
            return modelMapper.map(savedAddress, AddressResponse.class);
        }
    }

    @Override
    public void deleteAddress(AddressIdDto addressId, String userId) {
        Address addressToDelete = addressRepository.findById(addressId.getId())
                .orElseThrow(() -> new DataNotFoundException(String.format("Address with id = %s not found.", addressId.getId())));
        addressRepository.delete(addressToDelete);
        log.info("[ON deleteAddress] :: address with id {} was deleted.", addressId.getId());
    }

    @Override
    public List<AddressResponse> getUserAddresses(String userId) {
        List<Address> addresses = addressRepository.findAllByUserId(userId);

        return addresses.stream().map( address -> modelMapper.map(address, AddressResponse.class)).toList();
    }
}
