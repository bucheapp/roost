package io.github.bucheapp.roost.dto.request;

import java.time.LocalDate;

import org.springframework.web.multipart.MultipartFile;

import io.github.bucheapp.roost.models.Gender;

public class UpdateProfileRequest {
	private String bio;
	private MultipartFile iconFile;
	private Gender gender;
	private LocalDate dateOfBirth;
	private String address;
	private String githubUrl;
	
	public String getBio() {
		return bio;
	}
	public void setBio(String bio) {
		this.bio = bio;
	}
	public MultipartFile getIconFile() {
		return iconFile;
	}
	public void setIconFile(MultipartFile iconFile) {
		this.iconFile = iconFile;
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
}
