package io.github.bucheapp.roost.dto.response.sw;

import io.github.bucheapp.roost.dto.response.CommunityResponse;
import io.github.bucheapp.roost.models.Community;
import io.github.bucheapp.roost.models.SWType;

public class CommunitySWResponse extends CommunityResponse {
	private SWType swType;
	
	public CommunitySWResponse(Community community,SWType swType) {
		super(community);
		this.swType = swType;
	}

	public SWType getSwType() {
		return swType;
	}

	public void setSwType(SWType swType) {
		this.swType = swType;
	}
}
