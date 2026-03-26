package io.github.bucheapp.roost.dto.response;

import io.github.bucheapp.roost.models.User;
import io.github.bucheapp.roost.models.UserState;

public class UserPrivateResponse implements UserResponse {
	private String name;
	private String email;
	private UserState state;
	
	public UserPrivateResponse(User user) {
		this.name = user.getName();
		this.email = user.getEmail();
		this.state = user.getState();
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

	public UserState getState() {
		return state;
	}

	public void setState(UserState state) {
		this.state = state;
	}
}
