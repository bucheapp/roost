package io.github.bucheapp.roost.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UpdateUserRequest {
	@NotBlank(message="{user.name.notBlank}")
	@Size(min = 3, max = 20,message="{user.name.size}")
	@Pattern(regexp = "^[\\p{L}\\p{N}?!_・\\(\\)]+$",message="{user.name.pattern}")
	private String name;
	
	@Email(message="{email.invalid}")
	@Size(max = 255,message="{email.size}")
	@Pattern(regexp = ".*\\S.*",message="{email.pattern}")
	private String email;
	
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
}
