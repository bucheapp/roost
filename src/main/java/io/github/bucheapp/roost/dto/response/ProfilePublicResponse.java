package io.github.bucheapp.roost.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import io.github.bucheapp.roost.models.Gender;
import io.github.bucheapp.roost.models.Profile;

public class ProfilePublicResponse implements ProfileResponse {
	private String bio;
	private String iconUrl;
	private Gender gender;
	private LocalDate dateOfBirth;
	private String address;
	private String githubUrl;
	private LocalDateTime createdAt;
	
	public ProfilePublicResponse(Profile profile) {
		this.bio = profile.getBio();
		this.iconUrl = profile.getIconUrl();
		this.gender = profile.getGender();
		this.dateOfBirth = profile.getDateOfBirth();
		this.address = profile.getAddress();
		this.githubUrl = profile.getGithubUrl();
		this.createdAt = profile.getCreatedAt();
	}

	public String getBio() {
		return bio;
	}

	public void setBio(String bio) {
		this.bio = bio;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public LocalDate getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(LocalDate dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getGithubUrl() {
		return githubUrl;
	}

	public void setGithubUrl(String githubUrl) {
		this.githubUrl = githubUrl;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
}
