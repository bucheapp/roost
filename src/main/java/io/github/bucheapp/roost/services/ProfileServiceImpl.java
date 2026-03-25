package io.github.bucheapp.roost.services;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.bucheapp.roost.dto.request.ProfileUpdateRequest;
import io.github.bucheapp.roost.models.Profile;
import io.github.bucheapp.roost.models.User;
import io.github.bucheapp.roost.repositories.ProfileRepository;
import io.github.bucheapp.roost.repositories.UserRepository;

@Service
public class ProfileServiceImpl implements ProfileService {
	@Autowired
	ProfileRepository profileRepository;
	
	@Autowired
	UserRepository userRepository;

	@Override
	public Profile getProfileByUserId(long userId) {
		return profileRepository.findByUserId(userId)
				.orElseThrow(() -> new RuntimeException("プロフィールが存在しません"));
	}

	@Override
	public Profile getProfileByUserPublicId(long publicId) {
		User user = userRepository.findByPublicId(publicId)
				.orElseThrow(() -> new RuntimeException("ユーザが存在しません"));

		return profileRepository.findByUser(user)
				.orElseThrow(() -> new RuntimeException("プロフィールが存在しません"));
	}

	@Override
	@Transactional
	public Profile updateProfileByUserId(long userId, ProfileUpdateRequest updateRequest) {
		Profile profile = profileRepository.findByUserId(userId)
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
