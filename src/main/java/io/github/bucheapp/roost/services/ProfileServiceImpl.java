package io.github.bucheapp.roost.services;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.bucheapp.roost.dto.ProfileUpdateRequest;
import io.github.bucheapp.roost.models.Gender;
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
	public Mono<Profile> updateProfileById(long id, ProfileUpdateRequest updateRequest) {
		return profileRepository.findByUserId(id)
		.switchIfEmpty(Mono.error(new RuntimeException("プロフィールが存在しません")))
		.flatMap(profile -> {
			String bio = updateRequest.getBio();
			if(bio != null) {
				profile.setBio(bio);
			}
			String iconUrl = updateRequest.getIconUrl();
			if(iconUrl != null) {
				profile.setIconUrl(iconUrl);
			}
			Gender gender = updateRequest.getGender();
			if(gender != null) {
				profile.setGender(gender);
			}
			LocalDate dateOfBirth = updateRequest.getDateOfBirth();
			if(dateOfBirth != null) {
				profile.setDateOfBirth(dateOfBirth);
			}
			String phoneNumber = updateRequest.getPhoneNumber();
			if(phoneNumber != null) {
				profile.setPhoneNumber(phoneNumber);
			}
			String address = updateRequest.getAddress();
			if(address != null) {
				profile.setAddress(address);
			}
			String githubUrl = updateRequest.getGithubUrl();
			if(githubUrl != null) {
				profile.setGithubUrl(githubUrl);
			}
			LocalDateTime createdAt = updateRequest.getCreatedAt();
			if(createdAt != null) {
				profile.setCreatedAt(createdAt);
			}
			
			return Mono.just(profileRepository.save(profile));
		});
	}
}
