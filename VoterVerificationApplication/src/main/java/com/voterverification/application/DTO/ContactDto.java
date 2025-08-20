package com.voterverification.application.DTO;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactDto {

	private UUID contactId;
	
	private String mobile;

	private String email;

}
