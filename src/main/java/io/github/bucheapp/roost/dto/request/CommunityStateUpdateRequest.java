package io.github.bucheapp.roost.dto.request;

import io.github.bucheapp.roost.models.CommunityState;

public class CommunityStateUpdateRequest {
	private CommunityState state;

	public CommunityState getState() {
		return state;
	}

	public void setState(CommunityState state) {
		this.state = state;
	}
}
