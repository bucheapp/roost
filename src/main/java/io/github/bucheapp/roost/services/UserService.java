package io.github.bucheapp.roost.services;

import io.github.bucheapp.roost.dto.SignupRequest;
import io.github.bucheapp.roost.dto.SignupResponse;
import reactor.core.publisher.Mono;

public interface UserService {
	Mono<SignupResponse> register(SignupRequest req);
}
