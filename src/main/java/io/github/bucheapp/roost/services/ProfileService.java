package io.github.bucheapp.roost.services;

import io.github.bucheapp.roost.dto.ProfileUpdateRequest;
import io.github.bucheapp.roost.models.Profile;

public interface ProfileService {
	Profile getProfileById(long id);
	Profile getProfileByPublicId(long publicId);
	Profile updateProfileById(long id,ProfileUpdateRequest updateRequest);
}
