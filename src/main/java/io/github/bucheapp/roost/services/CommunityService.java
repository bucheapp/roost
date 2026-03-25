package io.github.bucheapp.roost.services;

import io.github.bucheapp.roost.dto.request.CommunityRequest;
import io.github.bucheapp.roost.dto.request.CommunityStatusUpdateRequest;
import io.github.bucheapp.roost.dto.request.CommunityUpdateRequest;
import io.github.bucheapp.roost.models.Community;

public interface CommunityService {
	Community getCommunityByPublicId(long publicId);
	Community createCommunity(long id,CommunityRequest req);
	Community updateCommunityByPublicId(long publicId,CommunityUpdateRequest req);
	Community updateCommunityStatusByPublicId(long publicId,CommunityStatusUpdateRequest req);
	void setFrozen(long publicId,boolean frozen);
	void setArchived(long publicId,boolean archived);
}
