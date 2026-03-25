package io.github.bucheapp.roost.dto.response;

import io.github.bucheapp.roost.models.User;

public class UserPrivateResponse implements UserResponse {
	private String name;
	private String email;
	boolean frozen;
	
	public UserPrivateResponse(User user) {
		this.name = user.getName();
		this.email = user.getEmail();
		this.frozen = user.isFrozen();
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isFrozen() {
		return frozen;
	}

	public void setFrozen(boolean frozen) {
		this.frozen = frozen;
	}
}
