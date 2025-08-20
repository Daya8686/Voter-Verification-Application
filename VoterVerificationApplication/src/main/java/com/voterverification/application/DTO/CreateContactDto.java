package com.voterverification.application.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateContactDto {
	
	@NotBlank(message = "Mobile number can't be left blank!!")
    @Pattern(regexp = "^[0-9]{10}$", message = "Mobile number must be exactly 10 digits long.")
    private String mobile;

    
    @Email(message = "Email must be in a valid format (e.g., user@example.com).")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@(gmail\\.com|yahoo\\.com|outlook\\.com)$", message = "Invalid email provider. Only gmail.com, yahoo.com, or outlook.com are allowed.")
    private String email;
}
