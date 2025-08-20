package com.voterverification.application.exception;

import org.springframework.http.HttpStatus;

public class ContactServiceExceptionHandler extends RuntimeException {
private HttpStatus httpStatus;
	
	public ContactServiceExceptionHandler (String message, HttpStatus httpStatus) {
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
