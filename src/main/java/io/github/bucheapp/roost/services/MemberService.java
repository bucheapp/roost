package io.github.bucheapp.roost.services;

import java.io.IOException;
import java.util.Set;

import io.github.bucheapp.roost.dto.request.MemberRequest;
import io.github.bucheapp.roost.dto.request.MemberSearchRequest;
import io.github.bucheapp.roost.models.Member;

public interface MemberService {
	Set<Member> joinMember(long publicId,MemberRequest req) throws IOException ;
	Set<Member> getMember(long publicId,MemberSearchRequest req);
	void leaveMember(long publicId);
	void kickMember(long publicId,long userPublicId);
	void banMember(long publicId,long userPublicId);
	void unbanMember(long publicId,long userPublicId);
	void approveMember(long publicId,long userPublicId);
}
