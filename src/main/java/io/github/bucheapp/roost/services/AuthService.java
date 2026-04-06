package io.github.bucheapp.roost.services;

import io.github.bucheapp.roost.dto.request.LoginRequest;
import io.github.bucheapp.roost.dto.request.SignupRequest;
import io.github.bucheapp.roost.dto.response.LoginResponse;
import io.github.bucheapp.roost.dto.response.SignupResponse;

public interface AuthService {
	SignupResponse register(SignupRequest req);
	LoginResponse login(LoginRequest req);
	void logout(String refreshTokenText);
	String refresh(String refreshTokenText);
	public boolean checkRefreshToken(String refreshTokenText);
}
