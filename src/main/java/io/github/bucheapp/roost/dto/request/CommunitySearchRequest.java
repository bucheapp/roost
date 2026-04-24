package io.github.bucheapp.roost.dto.request;

import java.util.Set;

import io.github.bucheapp.roost.models.CommunityProperty;
import io.github.bucheapp.roost.models.CommunityState;
import io.github.bucheapp.roost.models.CommunityType;

public class CommunitySearchRequest {
	private CommunityState state;
	private CommunityType type;
	private Set<CommunityProperty> properties;
	String name;
	
	public CommunitySearchRequest(
			CommunityState state,
			CommunityType type,
			Set<CommunityProperty> properties,
			String name
			) {
		this.state = state;
		this.type = type;
		this.properties = properties;
		this.name = name;
	}
	
	public CommunityState getState() {
		return state;
	}
	public void setState(CommunityState state) {
		this.state = state;
	}
	public CommunityType getType() {
		return type;
	}
	public void setType(CommunityType type) {
		this.type = type;
	}
	public Set<CommunityProperty> getProperties() {
		return properties;
	}
	public void setProperties(Set<CommunityProperty> properties) {
		this.properties = properties;
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
