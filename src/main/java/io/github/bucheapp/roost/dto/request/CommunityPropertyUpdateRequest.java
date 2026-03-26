package io.github.bucheapp.roost.dto.request;

import java.util.HashSet;
import java.util.Set;

import io.github.bucheapp.roost.models.CommunityProperty;

public class CommunityPropertyUpdateRequest {
	private Set<CommunityProperty> properties = new HashSet<>();

	public Set<CommunityProperty> getProperties() {
		return properties;
	}

	public void setProperties(Set<CommunityProperty> properties) {
		this.properties = properties;
	}
}
