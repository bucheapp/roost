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

import io.github.bucheapp.roost.dto.ProfileUpdateRequest;
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

	@GetMapping("/{publicId}/profile")
	public ResponseEntity<Profile> getProfile(
			@PathVariable long publicId,
			@RequestHeader("Authorization") String authHeader) {

		Profile profile = profileService.getProfileByPublicId(publicId);

		return ResponseEntity.ok(profile);
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
