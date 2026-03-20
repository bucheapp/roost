package io.github.bucheapp.roost.services;

import io.github.bucheapp.roost.dto.LoginRequest;
import io.github.bucheapp.roost.dto.LoginResponse;
import io.github.bucheapp.roost.dto.SignupRequest;
import io.github.bucheapp.roost.dto.SignupResponse;
import io.github.bucheapp.roost.models.User;
import reactor.core.publisher.Mono;

public interface UserService {
	Mono<SignupResponse> register(SignupRequest req);
	Mono<User> getUserById(long id);
	Mono<Void> updateEmailById(long id,String email);
	Mono<Void> updatePasswordById(long id,String email);
	Mono<Void> deleteUserById(long id);
	Mono<LoginResponse> login(LoginRequest req);
	Mono<Void> logout(String refreshTokenText);
	Mono<String> refresh(String refreshTokenText);
}
