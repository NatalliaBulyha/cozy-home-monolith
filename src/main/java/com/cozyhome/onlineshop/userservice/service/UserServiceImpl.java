package com.cozyhome.onlineshop.userservice.service;

import com.cozyhome.onlineshop.dto.user.NewUserDto;
import com.cozyhome.onlineshop.dto.user.UserDto;
import com.cozyhome.onlineshop.userservice.model.Role;
import com.cozyhome.onlineshop.userservice.model.User;
import com.cozyhome.onlineshop.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final ModelMapper mapper;
    @Override
    public UserDto addNewUser(NewUserDto newUser) {
        User user = User.builder()
                .firstName(newUser.getFirstName())
                .lastName(newUser.getLastName())
                .email(newUser.getEmail())
                .password(passwordEncoder.encode(newUser.getPassword()))
                .phoneNumber(newUser.getPhoneNumber())
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .status(User.UserStatus.ACTIVE)
                .role(Role.CUSTOMER)
                .build();
        User savedUser = userRepository.save(user);
        return mapper.map(savedUser, UserDto.class);
    }
}
