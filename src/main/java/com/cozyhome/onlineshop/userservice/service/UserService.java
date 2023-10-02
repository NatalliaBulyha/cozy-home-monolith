package com.cozyhome.onlineshop.userservice.service;

import com.cozyhome.onlineshop.dto.user.NewUserDto;
import com.cozyhome.onlineshop.dto.user.UserAllInfoDto;
import com.cozyhome.onlineshop.dto.user.UserDto;
import com.cozyhome.onlineshop.dto.user.UserInfoDto;
import com.cozyhome.onlineshop.exception.ResourceNotFoundException;

import java.util.List;

public interface UserService {

    UserDto addNewUser(NewUserDto user) throws ResourceNotFoundException;
    List<UserAllInfoDto> findAllUsers();
    UserInfoDto findUserByEmail(String email);
}
