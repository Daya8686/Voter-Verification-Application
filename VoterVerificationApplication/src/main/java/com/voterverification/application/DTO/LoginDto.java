package com.voterverification.application.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDto {
	
	@NotBlank(message = "User is cannot left blank!!")
	 private String username;
	 
	@NotBlank(message = "User password field cannot be left blank")
	 private String password;

}
