package io.github.bucheapp.roost.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import io.github.bucheapp.roost.models.CommunityType;

public class CommunityRequest {
	@NotBlank(message="community.name.notBlank")
	@Size(min = 3,max = 30,message="community.name.size")
	@Pattern(regexp = "^[\\p{L}\\(\\)!?・]+$",message="community.name.pattern")
	private String name;
	private CommunityType type;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public CommunityType getType() {
		return type;
	}
	public void setType(CommunityType type) {
		this.type = type;
	}
}
