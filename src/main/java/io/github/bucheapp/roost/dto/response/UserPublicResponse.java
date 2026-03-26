package io.github.bucheapp.roost.dto.response;

import io.github.bucheapp.roost.models.User;
import io.github.bucheapp.roost.models.UserState;

public class UserPublicResponse implements UserResponse {
	private String name;
	private UserState state;
	
	public UserPublicResponse(User user) {
		this.name = user.getName();
		this.state = user.getState();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public UserState getState() {
		return state;
	}

	public void setState(UserState state) {
		this.state = state;
	}
}
