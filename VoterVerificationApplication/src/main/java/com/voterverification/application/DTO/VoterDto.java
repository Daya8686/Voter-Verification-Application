package com.voterverification.application.DTO;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoterDto {
	
	private UUID voterId;

	
	private Integer serialNumber; // S.No from Excel

	
	private String fullName;

	
	private String fatherName;

	
	private Integer voterAge;
	
	
	private boolean isVoted; // Default false
	
	
	private String houseNo;

	
	private String street;

	
	private String ward;

	
	private String village;

}
