package io.github.bucheapp.roost.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import io.github.bucheapp.roost.dto.response.ExceptionResponse;

@ControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<ExceptionResponse> handleResponseStatusException(ResponseStatusException ex) {
		ex.printStackTrace();
		String msg = ex.getReason();
		
		ExceptionResponse exceptionResponse = new ExceptionResponse(msg);
		
		return ResponseEntity.status(ex.getStatusCode()).body(exceptionResponse);
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ExceptionResponse> handleException(Exception ex) {
		ex.printStackTrace();
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new ExceptionResponse("Internal server error"));
	}
}