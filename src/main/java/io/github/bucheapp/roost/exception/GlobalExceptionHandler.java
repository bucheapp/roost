package io.github.bucheapp.roost.exception;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import io.github.bucheapp.roost.dto.response.ExceptionResponse;

@ControllerAdvice
public class GlobalExceptionHandler {
	@Autowired
	private MessageSource messageSource;
	
	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<ExceptionResponse> handleResponseStatusException(ResponseStatusException ex) {
		ex.printStackTrace();
		String msg = ex.getReason();
		
		ExceptionResponse exceptionResponse = new ExceptionResponse(msg);
		
		return ResponseEntity.status(ex.getStatusCode()).body(exceptionResponse);
	}

	@ExceptionHandler(NoResourceFoundException.class)
	public ResponseEntity<Void> handleNoResource(NoResourceFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ExceptionResponse> handleValidationException(MethodArgumentNotValidException ex) {

		String message = ex.getBindingResult()
			.getAllErrors()
			.stream()
			.map((ObjectError error) -> messageSource.getMessage(
					error,
					LocaleContextHolder.getLocale()
					))
			.findFirst()
			.orElse("Validation error");
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(new ExceptionResponse(message));
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ExceptionResponse> handleException(Exception ex) {
		ex.printStackTrace();
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new ExceptionResponse("Internal server error"));
	}
}