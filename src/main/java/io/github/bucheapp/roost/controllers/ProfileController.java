package io.github.bucheapp.roost.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.bucheapp.roost.dto.request.UpdateProfileRequest;
import io.github.bucheapp.roost.dto.response.ProfileResponse;
import io.github.bucheapp.roost.models.Profile;
import io.github.bucheapp.roost.services.ProfileService;

@RestController
@RequestMapping("api/users")
public class ProfileController {

	@Autowired
	private ProfileService profileService;

	@GetMapping("/{publicId}/profile")
	public ResponseEntity<ProfileResponse> getProfile(
			@PathVariable long publicId) {

		Profile profile = profileService.getProfile(publicId);
		ProfileResponse profileResponse = new ProfileResponse(profile);

		return ResponseEntity.ok(profileResponse);
	}
	
	@GetMapping("/me/profile")
	public ResponseEntity<ProfileResponse> getProfile() {
		Profile profile = profileService.getCurrentUserProfile();
		ProfileResponse profileResponse = new ProfileResponse(profile);
		
		return ResponseEntity.ok(profileResponse);
	}
	
	@PatchMapping("/me/profile")
	public ResponseEntity<Void> updateProfile(
			@RequestBody UpdateProfileRequest req) {

		profileService.updateCurrentUserProfile(req);

		return ResponseEntity.ok().build();
	}
}
