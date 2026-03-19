package io.github.bucheapp.roost.services;

import io.github.bucheapp.roost.dto.LoginRequest;
import io.github.bucheapp.roost.dto.LoginResponse;
import io.github.bucheapp.roost.dto.SignupRequest;
import io.github.bucheapp.roost.dto.SignupResponse;
import reactor.core.publisher.Mono;

public interface UserService {
	Mono<SignupResponse> register(SignupRequest req);
	Mono<LoginResponse> login(LoginRequest req);
	Mono<Void> logout(String refreshToken);
}
