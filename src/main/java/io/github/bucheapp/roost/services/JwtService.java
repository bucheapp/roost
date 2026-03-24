package io.github.bucheapp.roost.services;

import org.springframework.security.core.userdetails.UserDetails;

import io.github.bucheapp.roost.models.User;

public interface JwtService {
	String generateAccessToken(User user);
	String generateRefreshToken(User user);
	boolean validateToken(String token);
	boolean isTokenValid(String token, UserDetails userDetails);
	long extractUserId(String token);
}
