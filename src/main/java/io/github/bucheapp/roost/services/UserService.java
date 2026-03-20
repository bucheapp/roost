package io.github.bucheapp.roost.services;

import io.github.bucheapp.roost.dto.SignupRequest;
import io.github.bucheapp.roost.dto.SignupResponse;
import io.github.bucheapp.roost.models.User;
import reactor.core.publisher.Mono;

public interface UserService {
	Mono<SignupResponse> register(SignupRequest req);
	Mono<User> getUser(String accessToken);
	Mono<Void> updateUser(String accessToken);
	Mono<Void> deleteUser(String accessToken);
}
