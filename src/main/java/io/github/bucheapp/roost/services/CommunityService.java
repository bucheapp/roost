package io.github.bucheapp.roost.services;

import io.github.bucheapp.roost.dto.request.CommunityRequest;
import io.github.bucheapp.roost.dto.request.UpdateCommunityPropertyRequest;
import io.github.bucheapp.roost.dto.request.UpdateCommunityRequest;
import io.github.bucheapp.roost.dto.request.UpdateCommunityStateRequest;
import io.github.bucheapp.roost.models.Community;

public interface CommunityService {
	Community getCommunity(long publicId);
	Community createCommunity(CommunityRequest req);
	Community updateCommunity(long publicId,UpdateCommunityRequest req);
	Community updateCommunityState(long publicId,UpdateCommunityStateRequest req);
	Community updateCommunityProperty(long publicId,UpdateCommunityPropertyRequest req);
}
