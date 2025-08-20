package com.voterverification.application.exception;

import org.springframework.http.HttpStatus;

public class BatchServiceExceptionHandler extends RuntimeException {
private HttpStatus httpStatus;
	
	public BatchServiceExceptionHandler (String message, HttpStatus httpStatus) {
		super(message);
		this.httpStatus=httpStatus;
		
	}

	public HttpStatus getHttpStatus() {
		return httpStatus;
	}

	public void setHttpStatus(HttpStatus httpStatus) {
		this.httpStatus = httpStatus;
	} 

}
