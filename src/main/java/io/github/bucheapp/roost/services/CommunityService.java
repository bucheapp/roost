package io.github.bucheapp.roost.services;

import io.github.bucheapp.roost.dto.request.CommunityPropertyUpdateRequest;
import io.github.bucheapp.roost.dto.request.CommunityRequest;
import io.github.bucheapp.roost.dto.request.CommunityStateUpdateRequest;
import io.github.bucheapp.roost.dto.request.CommunityUpdateRequest;
import io.github.bucheapp.roost.models.Community;

public interface CommunityService {
	Community getCommunityByPublicId(long publicId);
	Community createCommunity(long id,CommunityRequest req);
	Community updateCommunityByPublicId(long publicId,CommunityUpdateRequest req);
	Community updateCommunityStateByPublicId(long publicId,CommunityStateUpdateRequest req);
	Community updateCommunityPropertyByPublicId(long publicId,CommunityPropertyUpdateRequest req);
}
