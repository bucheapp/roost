package io.github.bucheapp.roost.dto.request;

import io.github.bucheapp.roost.models.UserState;

public class UserStateUpdateRequest {
	private UserState state;

	public UserState getState() {
		return state;
	}

	public void setState(UserState state) {
		this.state = state;
	}
}
