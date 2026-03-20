package io.github.bucheapp.roost.services;

import io.github.bucheapp.roost.dto.ProfileUpdateRequest;
import io.github.bucheapp.roost.models.Profile;
import reactor.core.publisher.Mono;

public interface ProfileService {
	Mono<Profile> getProfile(long id);
	Mono<Void> updateProfile(String accessToken,ProfileUpdateRequest updateRequest);
}
