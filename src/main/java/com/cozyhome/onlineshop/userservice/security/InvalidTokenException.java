package com.cozyhome.onlineshop.userservice.security;

import io.jsonwebtoken.JwtException;

public class InvalidTokenException extends JwtException {

	 public InvalidTokenException(String message) {
	        super(message);
	    }
	 public InvalidTokenException(String message, Throwable cause) {
	        super(message);
	    }
}
