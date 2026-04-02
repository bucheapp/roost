package io.github.bucheapp.roost.dto.response;

import io.github.bucheapp.roost.models.ApprovalChat;
import io.github.bucheapp.roost.models.ChatType;

public class ApprovalChatResponse extends ChatResponse {
	long targetId;
	long approverId;
	
	public ApprovalChatResponse(ApprovalChat approvalChat) {
		super(approvalChat);
		this.targetId = approvalChat.getTargetUser().getPublicId();
		this.approverId = approvalChat.getApprover().getPublicId();
		setType(ChatType.APPROVAL);
	}
}
