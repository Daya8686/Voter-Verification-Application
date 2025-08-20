package com.voterverification.application.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voterverification.application.DTO.AdminRegistrationDto;
import com.voterverification.application.DTO.LoginDto;
import com.voterverification.application.DTO.UpdateUserInfoDto;
import com.voterverification.application.DTO.UserRegistrationDto;
import com.voterverification.application.exception.UserServiceExceptionHandler;
import com.voterverification.application.service.UserService;
import com.voterverification.application.util.ApiResponseHandler;
import com.voterverification.application.util.JWTTokenResponse;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class UserAuthController {
	
	@Autowired
	private UserService userService;
	
	@PostMapping("/register/admin")
	public ResponseEntity<ApiResponseHandler> adminRegistration(@Valid @RequestBody AdminRegistrationDto adminRegistrationDto) throws MessagingException{
		ApiResponseHandler registerAdmin = userService.registerAdmin(adminRegistrationDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(registerAdmin);
	}
	
	@PostMapping("/register")
	public ResponseEntity<ApiResponseHandler> userRegistration(@Valid @RequestBody UserRegistrationDto userRegistrationDto){
		ApiResponseHandler registerUser = userService.registerUser(userRegistrationDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(registerUser);
		
	}
	@PostMapping("/login")
	public ResponseEntity<?> userLogin(@RequestBody LoginDto loginDto){
		Object loginObject = userService.login(loginDto);
		
		if(loginObject instanceof ApiResponseHandler) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(loginObject);
		}
		else if(loginObject instanceof JWTTokenResponse) {
			return ResponseEntity.status(HttpStatus.OK).body(loginObject);
		}
		else {
			throw new UserServiceExceptionHandler("Invalid Type of object returned by service!", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping("/logout")
	public ResponseEntity<ApiResponseHandler> logoutUser(){
		ApiResponseHandler logoutUser = userService.logoutUser();
		return ResponseEntity.status(HttpStatus.OK).body(logoutUser);
	}
	
	@PutMapping("/verification/{token}")
	 public ResponseEntity<ApiResponseHandler> userVerification(@PathVariable String token){
		 ApiResponseHandler userVerification = userService.userVerification(token);
		 return ResponseEntity.status(HttpStatus.OK).body(userVerification);
	 }
	@PutMapping("/verification/{token}/{email}")
	public ResponseEntity<ApiResponseHandler> verifingAndUpdatingEmail(@PathVariable String token, @PathVariable String email ){
		ApiResponseHandler verifyAndUpdateEmail = userService.verifyAndUpdateEmail(token,email);
		return ResponseEntity.status(HttpStatus.OK).body(verifyAndUpdateEmail);
	}
	
	@GetMapping("/user-info")
	@PreAuthorize("hasAnyRole('ADMIN','VERIFIER')")
	public ResponseEntity<ApiResponseHandler> getUserInformation(){
		ApiResponseHandler userInformation = userService.getUserInformation();
		return ResponseEntity.status(HttpStatus.OK).body(userInformation);
	}
	
	@PutMapping("/update-user-info")
	@PreAuthorize("hasAnyRole('ADMIN','VERIFIER')")
	public ResponseEntity<ApiResponseHandler> updateUserInformation(@Valid @RequestBody UpdateUserInfoDto updateUserInfoDto){
		ApiResponseHandler updateUserInfo = userService.updateUserInfo(updateUserInfoDto);
		return ResponseEntity.status(HttpStatus.OK).body(updateUserInfo);
	}
	
	@GetMapping("/users")
	@PreAuthorize("hasAnyRole('ADMIN')")
	public ResponseEntity<ApiResponseHandler> getAllUsersAssociateWithElectionBoard(){
		ApiResponseHandler allUsersAssocaitedWithElectionsBoard = userService.getAllUsersAssocaitedWithElectionsBoard();
		return ResponseEntity.status(HttpStatus.OK).body(allUsersAssocaitedWithElectionsBoard);
	}
	
	@PutMapping("/users/logout/{userId}")
	@PreAuthorize("hasAnyRole('ADMIN')")
	public ResponseEntity<ApiResponseHandler> logoutVerifiers(@PathVariable UUID userId){
		ApiResponseHandler logoutUserByAdminUsingUserId = userService.logoutUserByAdminUsingUserId(userId);
		return ResponseEntity.status(HttpStatus.OK).body(logoutUserByAdminUsingUserId);
	}
	
	
}
