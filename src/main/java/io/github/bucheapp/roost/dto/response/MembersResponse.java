package io.github.bucheapp.roost.dto.response;

import java.util.Set;
import java.util.stream.Collectors;

import io.github.bucheapp.roost.models.Member;

public class MembersResponse {
	Set<MemberResponse> members;
	
	public MembersResponse(Set<Member> members) {
		this.members = members.stream()
				.map(MemberResponse::new)
				.collect(Collectors.toSet());
	}

	public Set<MemberResponse> getMembers() {
		return members;
	}

	public void setMembers(Set<MemberResponse> members) {
		this.members = members;
	}
}
