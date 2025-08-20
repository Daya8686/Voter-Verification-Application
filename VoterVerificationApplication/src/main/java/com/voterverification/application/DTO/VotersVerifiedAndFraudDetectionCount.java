package com.voterverification.application.DTO;

import java.util.List;

import com.voterverification.application.Entity.Voter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VotersVerifiedAndFraudDetectionCount {
	
	private long verifiedVoters;
	
	private long fraudDetectedCount;
	
	private List<VotersVerifiedDto> verifiedAndVoterVoters;

}
