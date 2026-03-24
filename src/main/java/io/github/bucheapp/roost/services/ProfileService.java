package io.github.bucheapp.roost.services;

import io.github.bucheapp.roost.dto.request.ProfileUpdateRequest;
import io.github.bucheapp.roost.models.Profile;

public interface ProfileService {
	Profile getProfileByUserId(long userId);
	Profile getProfileByUserPublicId(long publicId);
	Profile updateProfileByUserId(long userId,ProfileUpdateRequest updateRequest);
}
