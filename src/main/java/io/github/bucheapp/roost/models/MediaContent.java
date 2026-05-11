package io.github.bucheapp.roost.models;

import jakarta.persistence.Embeddable;

@Embeddable
public class MediaContent {
	private String mediaContentUrl;
	private MediaType type;
	
	public MediaContent(
			String mediaContentUrl,
			MediaType type
			) {
		this.mediaContentUrl = mediaContentUrl;
		this.type = type;
	}
	public String getMediaContentUrl() {
		return mediaContentUrl;
	}
	public void setMediaContentUrl(String mediaContentUrl) {
		this.mediaContentUrl = mediaContentUrl;
	}
	public MediaType getType() {
		return type;
	}
	public void setType(MediaType type) {
		this.type = type;
	}
}
