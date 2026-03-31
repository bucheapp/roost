package io.github.bucheapp.roost.services;

import java.util.Set;

import io.github.bucheapp.roost.dto.request.MemberRequest;
import io.github.bucheapp.roost.models.Member;

public interface MemberService {
	Set<Member> joinMember(long publicId,MemberRequest req);
	Set<Member> getMember(long publicId);
	void leaveMember(long publicId);
	void kickMember(long publicId,long userPublicId);
	void banMember(long publicId,long userPublicId);
}
