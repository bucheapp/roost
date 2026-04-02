package io.github.bucheapp.roost.dto.response;

import java.time.LocalDateTime;

import io.github.bucheapp.roost.models.Member;

public class MemberResponse {
	private long publicId;
	private LocalDateTime time;
	
	public MemberResponse(Member member) {
		this.publicId = member.getUser().getPublicId();
		this.time = member.getTime();
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
