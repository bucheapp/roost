package io.github.bucheapp.roost.services;

import java.util.List;

import org.springframework.data.domain.Pageable;

import io.github.bucheapp.roost.dto.request.CommunityRequest;
import io.github.bucheapp.roost.dto.request.CommunitySearchRequest;
import io.github.bucheapp.roost.dto.request.UpdateCommunityPropertyRequest;
import io.github.bucheapp.roost.dto.request.UpdateCommunityRequest;
import io.github.bucheapp.roost.dto.request.UpdateCommunityStateRequest;
import io.github.bucheapp.roost.models.Community;
import io.github.bucheapp.roost.models.Room;

public interface CommunityService {
	Community getCommunity(long publicId);
	List<Room> getRooms(long publicId);
	List<Community> search(
			CommunitySearchRequest req,
			Pageable pageable
			);
	void assignmentHost(long publicId,long userPublicId);
	Community createCommunity(CommunityRequest req);
	Community updateCommunity(long publicId,UpdateCommunityRequest req);
	Community updateCommunityState(long publicId,UpdateCommunityStateRequest req);
	Community updateCommunityProperty(long publicId,UpdateCommunityPropertyRequest req);
}
