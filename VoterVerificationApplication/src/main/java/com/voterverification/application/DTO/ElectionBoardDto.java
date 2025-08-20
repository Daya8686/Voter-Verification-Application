package com.voterverification.application.DTO;

import java.time.LocalDate;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ElectionBoardDto {
	
private UUID electionBoardId;
	
	
	private String electionName;
	
	
	private String electionAddress;
	
	
	private LocalDate electionDate;
	
	
	private String electionDay;

	private String electionBoardCode; // Unique 6-digit alphanumeric code


}
