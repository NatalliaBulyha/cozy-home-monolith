package com.cozyhome.onlineshop.userservice.service;

import com.cozyhome.onlineshop.dto.user.NewUserDto;
import com.cozyhome.onlineshop.dto.user.UserDto;

public interface UserService {

    UserDto addNewUser(NewUserDto user);
}
