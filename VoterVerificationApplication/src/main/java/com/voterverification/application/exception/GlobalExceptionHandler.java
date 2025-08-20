package com.voterverification.application.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.hibernate.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
	
	@Autowired
	private ErrorResponse errorResponse;
	
	 @Override
	    protected ResponseEntity<Object> handleMethodArgumentNotValid(
	            MethodArgumentNotValidException ex, 
	            HttpHeaders headers, 
	            HttpStatusCode status, 
	            WebRequest request) {

	        errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
	        errorResponse.setMessage("Validation error");
	        errorResponse.setTimestamp(LocalDateTime.now());

	        // Extract field errors and populate error details
	        Map<String, String> errors = ex.getBindingResult()
	                .getFieldErrors()
	                .stream()
	                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));

	        errorResponse.setErrors(errors);

	        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

//	@ExceptionHandler( TypeMismatchException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	
	public ErrorResponse handleTypeMismatchException( TypeMismatchException ex,
			BindingResult bindingResult) {
			errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
			errorResponse.setTimestamp(LocalDateTime.now());
			errorResponse.setMessage("Type Mismatch");
		return errorResponse;

	}
//		@ExceptionHandler(HttpMessageNotReadableException.class)
       public ErrorResponse handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
       
        errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        errorResponse.setMessage("JSON parse error: " + ex.getLocalizedMessage());
        errorResponse.setTimestamp(LocalDateTime.now());
        Throwable cause = ex.getCause();
        if (cause instanceof InvalidFormatException) {
            InvalidFormatException ife = (InvalidFormatException) cause;
            String fieldName = ife.getPath().stream()
                                   .map(ref -> ref.getFieldName())
                                   .findFirst()
                                   .orElse("Unknown field");

            String errorMessage = String.format("Invalid value for field '%s': %s", fieldName, ife.getValue());
            errorResponse.setMessage(errorMessage);
        } else {
            errorResponse.setMessage(ex.getLocalizedMessage());
        }

        return errorResponse;
    }
	
	@ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        errorResponse.setStatus(HttpStatus.CONFLICT.value());
        errorResponse.setMessage("Duplicate entry or data integrity violation");
        errorResponse.setTimestamp(LocalDateTime.now());
        // You can add more specific handling based on the exception message or type
        
        
        return errorResponse;
    }
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        
        // Extracting violations and formatting messages
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            String fieldName = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            errors.put(fieldName, message);
        }

        // Return a structured response
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
    
    @ExceptionHandler({JsonParseException.class, JsonMappingException.class})
    public ResponseEntity<Map<String, String>> handleJsonProcessingExceptions(Exception ex) {
        Map<String, String> errors = new HashMap<>();

        if (ex instanceof JsonParseException jsonParseException) {
            // Extract detailed parsing error information
            errors.put("details", String.format("JSON parsing error at line %d, column %d: %s",
                    jsonParseException.getLocation().getLineNr(),
                    jsonParseException.getLocation().getColumnNr(),
                    jsonParseException.getOriginalMessage()));
        } else if (ex instanceof JsonMappingException jsonMappingException) {
            // Extract the field name and the mapping error details
            String fieldName = jsonMappingException.getPath().stream()
                    .map(ref -> ref.getFieldName())
                    .findFirst()
                    .orElse("Unknown field");
            errors.put("error", "Invalid JSON field mapping");
            errors.put("details", String.format("Error at field '%s': %s", fieldName, jsonMappingException.getOriginalMessage()));
        } else {
            // Generic fallback for unexpected exceptions
            errors.put("error", "Unexpected JSON processing error");
            errors.put("details", ex.getMessage());
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
    @ExceptionHandler(UserServiceExceptionHandler.class)
   	public ResponseEntity<ErrorResponse> handleUserServiceExceptionHandler(UserServiceExceptionHandler ex) {
       	
   		errorResponse.setMessage(ex.getMessage());
   		errorResponse.setStatus(ex.getHttpStatus().value());
   		errorResponse.setTimestamp(LocalDateTime.now());
   		return new ResponseEntity<>(errorResponse, ex.getHttpStatus());
   	}
     
       @ExceptionHandler(ContactServiceExceptionHandler.class)
   	public ResponseEntity<ErrorResponse> handleContactServiceExceptionHandler(ContactServiceExceptionHandler ex) {
   		errorResponse.setMessage(ex.getMessage());
   		errorResponse.setStatus(ex.getHttpStatus().value());
   		errorResponse.setTimestamp(LocalDateTime.now());
   		return new ResponseEntity<>(errorResponse, ex.getHttpStatus());
   	}
       @ExceptionHandler
       public ResponseEntity<ErrorResponse> handleElectionBoardExceptionHandler(ElectionBoardExceptionHandler ex){
    	   errorResponse.setMessage(ex.getMessage());
    	   errorResponse.setStatus(ex.getHttpStatus().value());
    	   errorResponse.setTimestamp(LocalDateTime.now());
    	   return new ResponseEntity<>(errorResponse, ex.getHttpStatus());
       }
    
       @ExceptionHandler
       public ResponseEntity<ErrorResponse> handleVoterExceptionHandler(VoterExceptionHandler ex){
    	   errorResponse.setMessage(ex.getMessage());
    	   errorResponse.setStatus(ex.getHttpStatus().value());
    	   errorResponse.setTimestamp(LocalDateTime.now());
    	   return new ResponseEntity<>(errorResponse, ex.getHttpStatus());
       }
       
       @ExceptionHandler
       public ResponseEntity<ErrorResponse> handleBatchProcessExceptionHandler(BatchServiceExceptionHandler ex){
    	   errorResponse.setMessage(ex.getMessage());
    	   errorResponse.setStatus(ex.getHttpStatus().value());
    	   errorResponse.setTimestamp(LocalDateTime.now());
    	   return new ResponseEntity<>(errorResponse, ex.getHttpStatus());
       }
       
}

   
