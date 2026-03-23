package io.github.bucheapp.roost.dto.response;

import io.github.bucheapp.roost.models.User;

public class UserPublicResponse implements UserResponse {
	private String name;
	boolean frozen;
	
	public UserPublicResponse(User user) {
		this.name = user.getName();
		this.frozen = user.isFrozen();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isFrozen() {
		return frozen;
	}

	public void setFrozen(boolean frozen) {
		this.frozen = frozen;
	}
}
