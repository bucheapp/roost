package io.github.bucheapp.roost.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.github.bucheapp.roost.models.Community;
import io.github.bucheapp.roost.services.CommunityService;

@Component
public class CommunitySecurity {
	@Autowired
	private CommunityService communityService;
	
	@Autowired
	private AuthContext authContext;
	
	public boolean isHost(long publicId) {
		long userId = authContext.getCurrentUserId();
				
		Community community = communityService.getCommunity(publicId);
		
		return community.getHost().getId() == userId;
	}
}
