package io.github.bucheapp.roost.dto.response;

import io.github.bucheapp.roost.models.Chat;
import io.github.bucheapp.roost.models.SWType;

public class ChatSWResponse extends ChatResponse {
	private SWType swType;
	
	public ChatSWResponse(Chat chat,SWType swType) {
		super(chat);
		this.swType = swType;
	}
	
	public SWType getSwType() {
		return swType;
	}

	public void setSwType(SWType swType) {
		this.swType = swType;
	}
}
