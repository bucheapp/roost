package io.github.bucheapp.roost.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.bucheapp.roost.dto.request.ProfileUpdateRequest;
import io.github.bucheapp.roost.dto.response.ProfileResponse;
import io.github.bucheapp.roost.models.Profile;
import io.github.bucheapp.roost.services.JwtService;
import io.github.bucheapp.roost.services.ProfileService;

@RestController
@RequestMapping("api/profiles")
public class ProfileController {

	@Autowired
	private ProfileService profileService;

	@Autowired
	private JwtService jwtService;

	@GetMapping("/{publicId}")
	public ResponseEntity<ProfileResponse> getProfile(
			@PathVariable long publicId) {

		Profile profile = profileService.getProfileByPublicId(publicId);
		ProfileResponse profileResponse = new ProfileResponse(profile);

		return ResponseEntity.ok(profileResponse);
	}
	
	@GetMapping("/me")
	public ResponseEntity<ProfileResponse> getProfile(
			@RequestHeader("Authorization") String authHeader) {
		String token = authHeader.substring(7);
		long id = jwtService.extractUserId(token);
		
		Profile profile = profileService.getProfileByPublicId(id);
		ProfileResponse profileResponse = new ProfileResponse(profile);
		
		return ResponseEntity.ok(profileResponse);
	}
	
	@PatchMapping("/me/profile")
	public ResponseEntity<Void> updateProfile(
			@RequestHeader("Authorization") String authHeader,
			@RequestBody ProfileUpdateRequest body) {

		String token = authHeader.substring(7);
		long id = jwtService.extractUserId(token);

		profileService.updateProfileById(id, body);

		return ResponseEntity.ok().build();
	}
}
