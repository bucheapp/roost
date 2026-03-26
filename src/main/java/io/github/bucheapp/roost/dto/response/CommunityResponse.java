package io.github.bucheapp.roost.dto.response;

import java.time.LocalDateTime;
import java.util.Set;

import io.github.bucheapp.roost.models.Community;
import io.github.bucheapp.roost.models.CommunityProperty;
import io.github.bucheapp.roost.models.CommunityState;
import io.github.bucheapp.roost.models.CommunityType;

public class CommunityResponse {
	private CommunityType type;
	private CommunityState state;
	private Set<CommunityProperty> properties;
	private LocalDateTime createdAt;
	private LocalDateTime archivedAt;
	
	public CommunityResponse(Community community) {
		this.type = community.getType();
		this.state = community.getState();
		this.properties = community.getProperties();
		this.createdAt = community.getCreatedAt();
		this.archivedAt = community.getArchivedAt();
	}

	public CommunityType getType() {
		return type;
	}

	public void setType(CommunityType type) {
		this.type = type;
	}

	public CommunityState getState() {
		return state;
	}

	public void setState(CommunityState state) {
		this.state = state;
	}

	public Set<CommunityProperty> getProperties() {
		return properties;
	}

	public void setProperties(Set<CommunityProperty> properties) {
		this.properties = properties;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getArchivedAt() {
		return archivedAt;
	}

	public void setArchivedAt(LocalDateTime archivedAt) {
		this.archivedAt = archivedAt;
	}
}
