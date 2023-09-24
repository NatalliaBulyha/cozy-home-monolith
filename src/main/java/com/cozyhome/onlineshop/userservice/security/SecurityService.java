package com.cozyhome.onlineshop.userservice.security;

public interface SecurityService {

	boolean isAuthenticated(String username, String password);
}
