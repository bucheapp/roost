package io.github.bucheapp.roost.services;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.bucheapp.roost.dto.ProfileUpdateRequest;
import io.github.bucheapp.roost.models.Profile;
import io.github.bucheapp.roost.repositories.ProfileRepository;

@Service
public class ProfileServiceImpl implements ProfileService {
	@Autowired
	ProfileRepository profileRepository;

	@Override
	public Profile getProfileById(long id) {
		return profileRepository.findByUserId(id)
				.orElseThrow(() -> new RuntimeException("プロフィールが存在しません"));
	}

	@Override
	public Profile getProfileByPublicId(long id) {
		return profileRepository.findByUserId(id)
				.orElseThrow(() -> new RuntimeException("プロフィールが存在しません"));
	}

	@Override
	@Transactional
	public Profile updateProfileById(long id, ProfileUpdateRequest updateRequest) {
		Profile profile = profileRepository.findByUserId(id)
				.orElseThrow(() -> new RuntimeException("プロフィールが存在しません"));

		if (updateRequest.getBio() != null) profile.setBio(updateRequest.getBio());
		if (updateRequest.getIconUrl() != null) profile.setIconUrl(updateRequest.getIconUrl());
		if (updateRequest.getGender() != null) profile.setGender(updateRequest.getGender());
		if (updateRequest.getDateOfBirth() != null) profile.setDateOfBirth(updateRequest.getDateOfBirth());
		if (updateRequest.getPhoneNumber() != null) profile.setPhoneNumber(updateRequest.getPhoneNumber());
		if (updateRequest.getAddress() != null) profile.setAddress(updateRequest.getAddress());
		if (updateRequest.getGithubUrl() != null) profile.setGithubUrl(updateRequest.getGithubUrl());
		if (updateRequest.getCreatedAt() != null) profile.setCreatedAt(updateRequest.getCreatedAt());

		return profileRepository.save(profile);
	}
}
