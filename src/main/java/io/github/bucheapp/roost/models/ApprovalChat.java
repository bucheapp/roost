package io.github.bucheapp.roost.models;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;

import io.github.bucheapp.roost.dto.response.ApprovalChatResponse;

@Entity
@DiscriminatorValue("APPROVAL")
public class ApprovalChat extends Chat {
	@ManyToOne
	@JoinColumn(name = "target_user_id")
	@NotNull
	User targetUser;
	
	@ManyToOne
	@JoinColumn(name = "approver_id")
	User approver;
	
	public User getTargetUser() {
		return targetUser;
	}

	public void setTargetUser(User targetUser) {
		this.targetUser = targetUser;
	}

	public User getApprover() {
		return approver;
	}

	public void setApprover(User approver) {
		this.approver = approver;
	}

	@Override
	public ApprovalChatResponse toResponse() {
		ApprovalChatResponse res = new ApprovalChatResponse(this);
		return res;
	}
}
