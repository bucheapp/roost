package io.github.bucheapp.roost.services;

import io.github.bucheapp.roost.dto.request.UpdateProfileRequest;
import io.github.bucheapp.roost.models.Profile;

public interface ProfileService {
	Profile getCurrentUserProfile();
	Profile getProfile(long publicId);
	Profile updateCurrentUserProfile(UpdateProfileRequest req);
}
