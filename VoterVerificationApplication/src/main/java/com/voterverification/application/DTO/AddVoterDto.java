package com.voterverification.application.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddVoterDto {
	
	private Integer serialNumber; // S.No from Excel

	
	private String fullName;

	
	private String fatherName;

	
	private Integer voterAge;

	
	private String houseNo;

	
	private String street;

	
	private String ward;

	
	private String village;


//	private String uniqueCode; // Generated at Spring Batch process

}
