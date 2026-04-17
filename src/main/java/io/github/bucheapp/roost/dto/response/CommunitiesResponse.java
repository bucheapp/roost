package io.github.bucheapp.roost.dto.response;

import java.util.List;

import io.github.bucheapp.roost.models.Community;

public class CommunitiesResponse {
	List<CommunityResponse> communities;
	
	public CommunitiesResponse(
			List<Community> communities
			) {
		this.communities = communities.stream()
				.map(CommunityResponse::new)
				.toList();
	}
}
