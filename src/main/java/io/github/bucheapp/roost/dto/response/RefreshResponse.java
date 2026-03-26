package io.github.bucheapp.roost.dto.response;

public class RefreshResponse {
	private String accessToken;
	
	public RefreshResponse(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
}
