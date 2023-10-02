package com.cozyhome.onlineshop.userservice.service;

import com.cozyhome.onlineshop.dto.user.NewUserDto;
import com.cozyhome.onlineshop.dto.user.UserAllInfoDto;
import com.cozyhome.onlineshop.dto.user.UserDto;
import com.cozyhome.onlineshop.dto.user.UserInfoDto;
import com.cozyhome.onlineshop.exception.DataExistsException;
import com.cozyhome.onlineshop.exception.DataNotExistException;
import com.cozyhome.onlineshop.exception.ResourceNotFoundException;
import com.cozyhome.onlineshop.userservice.model.Role;
import com.cozyhome.onlineshop.userservice.model.User;
import com.cozyhome.onlineshop.userservice.repository.RoleRepository;
import com.cozyhome.onlineshop.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final String CUSTOMER = "CUSTOMER";
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final ModelMapper mapper;
    @Override
    public UserDto addNewUser(NewUserDto newUser) {
        boolean isUserExist = userRepository.existsByEmail(newUser.getEmail());
        if (!isUserExist) {
            throw new DataExistsException(String.format("User with email = %s already exists.", newUser.getEmail()));
        }

        Role role = roleRepository.findByName(CUSTOMER)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Role %s doesn't found.", CUSTOMER)));
        User user = User.builder()
                .firstName(newUser.getFirstName())
                .lastName(newUser.getLastName())
                .email(newUser.getEmail())
                .password(passwordEncoder.encode(newUser.getPassword()))
                .phoneNumber(newUser.getPhoneNumber())
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .status(User.UserStatus.ACTIVE)
                .role(role)
                .build();
        User savedUser = userRepository.save(user);
        return mapper.map(savedUser, UserDto.class);
    }

    @Override
    public List<UserAllInfoDto> findAllUsers() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            throw new DataNotExistException("DB doesn't have users.");
        } else {
            return users.stream().map(user -> mapper.map(user, UserAllInfoDto.class)).toList();
        }
    }

    @Override
    public UserInfoDto findUserByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            return mapper.map(user.get(), UserInfoDto.class);
        } else {
            throw new DataNotExistException(String.format("User with email = %s doesn't exist.", email));
        }
    }
}
