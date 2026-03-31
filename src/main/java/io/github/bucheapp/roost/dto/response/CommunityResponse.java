package io.github.bucheapp.roost.dto.response;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import io.github.bucheapp.roost.models.Community;
import io.github.bucheapp.roost.models.CommunityProperty;
import io.github.bucheapp.roost.models.CommunityState;
import io.github.bucheapp.roost.models.CommunityType;
import io.github.bucheapp.roost.models.HostHistory;

public class CommunityResponse {
	private CommunityType type;
	private CommunityState state;
	private Set<CommunityProperty> properties;
	private long publicId;
	private LocalDateTime createdAt;
	private LocalDateTime archivedAt;
	private String name;
	private Set<HostHistoryResponse> hostHistoryResponses;
	
	public CommunityResponse(Community community) {
		this.type = community.getType();
		this.state = community.getState();
		this.properties = community.getProperties();
		this.publicId = community.getPublicId();
		this.createdAt = community.getCreatedAt();
		this.archivedAt = community.getArchivedAt();
		this.name = community.getName();
		
		this.hostHistoryResponses = new HashSet<>();
		
		for(HostHistory operatoryHistory : community.getOperatorHistory()) {
			this.hostHistoryResponses.add(
					new HostHistoryResponse(
							operatoryHistory
							)
					);
		}
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

	public long getPublicId() {
		return publicId;
	}

	public void setPublicId(long publicId) {
		this.publicId = publicId;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}

class HostHistoryResponse {
	private long publicId;
	private LocalDateTime time;
	
	public HostHistoryResponse(
			HostHistory hostHistory
			) {
		this.publicId = hostHistory.getUser().getPublicId();
		this.time = hostHistory.getTime();
	}

	public long getPublicId() {
		return publicId;
	}

	public void setPublicId(long publicId) {
		this.publicId = publicId;
	}

	public LocalDateTime getTime() {
		return time;
	}

	public void setTime(LocalDateTime time) {
		this.time = time;
	}
}
