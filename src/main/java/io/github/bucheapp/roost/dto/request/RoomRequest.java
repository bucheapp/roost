package io.github.bucheapp.roost.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RoomRequest {
	@NotBlank(message="{room.name.notBlank}")
	@Size(min = 1,max = 10,message="{room.name.size}")
	@Pattern(regexp = "^[\\p{L}\\(\\)!?・]+$",message="{room.name.pattern}")
	String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
