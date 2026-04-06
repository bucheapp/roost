package io.github.bucheapp.roost.services;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import io.github.bucheapp.roost.dto.request.UpdateProfileRequest;
import io.github.bucheapp.roost.models.Profile;
import io.github.bucheapp.roost.models.User;
import io.github.bucheapp.roost.repositories.ProfileRepository;
import io.github.bucheapp.roost.repositories.UserRepository;
import io.github.bucheapp.roost.security.AuthContext;
import io.github.bucheapp.roost.util.MessageUtil;

@Service
public class ProfileServiceImpl implements ProfileService {
	@Autowired
	private ProfileRepository profileRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private AuthContext authContext;
	
	@Autowired
	private MessageUtil messageUtil;

	@Override
	public Profile getCurrentUserProfile() {
		long userId = authContext.getCurrentUserId();
		
		return profileRepository.findByUserId(userId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, messageUtil.get("profile.notfound")));
	}

	@Override
	public Profile getProfile(long publicId) {
		User user = userRepository.findByPublicId(publicId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, messageUtil.get("user.notfound")));

		return profileRepository.findByUser(user)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, messageUtil.get("profile.notfound")));
	}

	@Override
	@Transactional
	public Profile updateCurrentUserProfile(UpdateProfileRequest req) {
		long userId = authContext.getCurrentUserId();
		
		Profile profile = profileRepository.findByUserId(userId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, messageUtil.get("profile.notfound")));

		if (req.getBio() != null) profile.setBio(req.getBio());
		if (req.getIconUrl() != null) profile.setIconUrl(req.getIconUrl());
		if (req.getGender() != null) profile.setGender(req.getGender());
		if (req.getDateOfBirth() != null) profile.setDateOfBirth(req.getDateOfBirth());
		if (req.getPhoneNumber() != null) profile.setPhoneNumber(req.getPhoneNumber());
		if (req.getAddress() != null) profile.setAddress(req.getAddress());
		if (req.getGithubUrl() != null) profile.setGithubUrl(req.getGithubUrl());
		if (req.getCreatedAt() != null) profile.setCreatedAt(req.getCreatedAt());

		return profileRepository.save(profile);
	}
}
