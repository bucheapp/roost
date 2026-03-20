package io.github.bucheapp.roost.services;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.bucheapp.roost.dto.ProfileUpdateRequest;
import io.github.bucheapp.roost.models.Profile;
import io.github.bucheapp.roost.repositories.ProfileRepository;
import reactor.core.publisher.Mono;

@Service
public class ProfileServiceImpl implements ProfileService {
	@Autowired
	ProfileRepository profileRepository;
	
	@Override
	public Mono<Profile> getProfileById(long id) {
		return profileRepository.findByUserId(id)
				.switchIfEmpty(Mono.error(new RuntimeException("プロフィールが存在しません")))
				.flatMap(profile -> {
					return Mono.just(profile);
				});
	}

	@Override
	@Transactional
	public Mono<Void> updateProfileById(long id, ProfileUpdateRequest updateRequest) {
		profileRepository.findByUserId(id)
		.switchIfEmpty(Mono.error(new RuntimeException("プロフィールが存在しません")))
		.flatMap(profile -> {
			
		});
	}
}
