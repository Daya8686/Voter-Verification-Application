package com.voterverification.application.exception;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
	private int status;
	private String message;
	@JsonFormat(pattern = "dd-MM-yyyy'T'HH:mm:ss.SSS")
	private LocalDateTime timestamp;
	private Map<String, String> errors; // Field errors with details

	
	

}
