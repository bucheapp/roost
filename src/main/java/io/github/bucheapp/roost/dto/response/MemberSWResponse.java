package io.github.bucheapp.roost.dto.response;

import java.util.Set;
import java.util.stream.Collectors;

import io.github.bucheapp.roost.models.Member;
import io.github.bucheapp.roost.models.SWType;

public class MemberSWResponse {
	private SWType swType;
	private Set<MemberResponse> memberResponses;
	
	public MemberSWResponse(Set<Member> members,SWType swType) {
		this.swType = swType;
		this.memberResponses = members.stream()
				.map(MemberResponse::new)
				.collect(Collectors.toSet());
	}
	
	public SWType getSwType() {
		return swType;
	}

	public void setSwType(SWType swType) {
		this.swType = swType;
	}

	public Set<MemberResponse> getMemberResponses() {
		return memberResponses;
	}

	public void setMemberResponses(Set<MemberResponse> memberResponses) {
		this.memberResponses = memberResponses;
	}
}
