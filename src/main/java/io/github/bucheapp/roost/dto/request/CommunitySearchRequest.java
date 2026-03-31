package io.github.bucheapp.roost.dto.request;

import java.util.Set;

import io.github.bucheapp.roost.models.CommunityProperty;

public class CommunitySearchRequest {
	private Set<CommunityProperty> properties;

	private String sort;

	private Integer size;

	private Integer page;

	public Set<CommunityProperty> getProperties() {
		return properties;
	}

	public void setProperties(Set<CommunityProperty> properties) {
		this.properties = properties;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}
}
