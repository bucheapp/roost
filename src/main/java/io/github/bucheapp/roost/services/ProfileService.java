package io.github.bucheapp.roost.services;

import io.github.bucheapp.roost.dto.ProfileUpdateRequest;
import io.github.bucheapp.roost.models.Profile;
import reactor.core.publisher.Mono;

public interface ProfileService {
	Mono<Profile> getProfileById(long id);
	Mono<Profile> getProfileByPublicId(long publicId);
	Mono<Profile> updateProfileById(long id,ProfileUpdateRequest updateRequest);
}
