package io.github.bucheapp.roost.dto.response;

public class ExceptionResponse {
	String msg;
	
	public ExceptionResponse(String msg) {
		this.msg = msg;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
}
