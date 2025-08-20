package com.voterverification.application.exception;

import org.springframework.http.HttpStatus;


public class ElectionBoardExceptionHandler extends RuntimeException{
	
	private HttpStatus httpStatus;
	
	public ElectionBoardExceptionHandler (String message, HttpStatus httpStatus) {
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
