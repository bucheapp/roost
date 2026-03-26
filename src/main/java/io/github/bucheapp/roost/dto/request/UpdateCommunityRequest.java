package io.github.bucheapp.roost.dto.request;

import io.github.bucheapp.roost.models.CommunityType;

public class UpdateCommunityRequest {
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
