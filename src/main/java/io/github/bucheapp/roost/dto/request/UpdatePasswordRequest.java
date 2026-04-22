package io.github.bucheapp.roost.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UpdatePasswordRequest {
	String currentPassword;
	@NotBlank(message="password.notBlank")
	@Size(min = 8, max = 18,message="password.size")
	@Pattern(regexp = "^[\\da-zA-Z_]+$",message="password.pattern")
	String newPassword;
	
	public String getCurrentPassword() {
		return currentPassword;
	}
	public void setCurrentPassword(String currentPassword) {
		this.currentPassword = currentPassword;
	}
	public String getNewPassword() {
		return newPassword;
	}
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
}
