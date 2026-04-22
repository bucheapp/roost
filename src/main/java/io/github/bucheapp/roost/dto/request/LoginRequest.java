package io.github.bucheapp.roost.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LoginRequest {
	@NotBlank(message="user.name.notBlank")
	@Size(min = 3, max = 20,message="user.name.size")
	private String name;
	
	@NotBlank(message="password.notBlank")
	@Size(min = 8, max = 18,message="password.size")
	private String password;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}
