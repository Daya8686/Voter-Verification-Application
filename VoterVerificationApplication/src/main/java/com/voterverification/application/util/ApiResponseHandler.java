package com.voterverification.application.util;

public class ApiResponseHandler {

	private Object data;
	private int httpStatus;
	private String message;

	public ApiResponseHandler(Object data, int httpStatus, String message) {
		super();
		this.data = data;
		this.httpStatus = httpStatus;
		this.message = message;
	}
	public ApiResponseHandler() {}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public int getHttpStatus() {
		return httpStatus;
	}

	public void setHttpStatus(int httpStatus) {
		this.httpStatus = httpStatus;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "ApiResponseHandler [data=" + data + ", httpStatus=" + httpStatus + ", message=" + message + "]";
	}

}
