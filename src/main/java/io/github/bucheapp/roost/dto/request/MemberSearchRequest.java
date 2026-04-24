package io.github.bucheapp.roost.dto.request;

import io.github.bucheapp.roost.models.MemberState;

public class MemberSearchRequest {
	MemberState state;
	
	public MemberSearchRequest(MemberState state) {
		this.state = state;
	}

	public MemberState getState() {
		return state;
	}

	public void setState(MemberState state) {
		this.state = state;
	}
}
