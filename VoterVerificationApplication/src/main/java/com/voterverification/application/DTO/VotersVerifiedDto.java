package com.voterverification.application.DTO;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VotersVerifiedDto {

	
	private Integer serialNumber; // S.No from Excel

	
	private String fullName;

	
	private String fatherName;

	
	private Integer voterAge;
	
	private LocalDateTime verifiedTime;

}
