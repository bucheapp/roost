package io.github.bucheapp.roost.services;

import io.github.bucheapp.roost.models.User;

public interface JwtService {
    String generateAccessToken(User user);
    String generateRefreshToken(User user);
    boolean validateToken(String token);
    long extractUserId(String token);
}
