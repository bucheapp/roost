package io.github.bucheapp.roost.dto.request;

import java.util.Set;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CreateUserRequest {
	@NotBlank(message="user.name.notBlank")
	@Size(min = 3, max = 20,message="user.name.size")
	@Pattern(regexp = "^[\\p{L}\\p{N}?!_・\\(\\)]+$",message="user.name.pattern")
	private String name;
	
	@Email(message="email.invalid")
	@Size(max = 255,message="email.size")
	@Pattern(regexp = ".*\\S.*",message="email.pattern")
	private String email;
	
	@NotBlank(message="password.notBlank")
	@Size(min = 8, max = 18,message="password.size")
	@Pattern(regexp = "^[\\da-zA-Z_]+$",message="password.pattern")
	private String password;
	private Set<String> permissions;
	
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
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Set<String> getPermissions() {
		return permissions;
	}
	public void setPermissions(Set<String> permissions) {
		this.permissions = permissions;
	}
}
