package io.github.bucheapp.roost.services;

import io.github.bucheapp.roost.dto.LoginRequest;
import io.github.bucheapp.roost.dto.LoginResponse;
import io.github.bucheapp.roost.dto.SignupRequest;
import io.github.bucheapp.roost.dto.SignupResponse;
import io.github.bucheapp.roost.models.User;

public interface UserService {
	SignupResponse register(SignupRequest req);
	User getUserById(long id);
	void updateEmailById(long id,String email);
	void updatePasswordById(long id,String email);
	void deleteUserById(long id);
	LoginResponse login(LoginRequest req);
	void logout(String refreshTokenText);
	String refresh(String refreshTokenText);
}
