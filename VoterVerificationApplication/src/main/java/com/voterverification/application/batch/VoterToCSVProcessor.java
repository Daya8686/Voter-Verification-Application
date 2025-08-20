package com.voterverification.application.batch;
import java.time.format.DateTimeFormatter;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.voterverification.application.DTO.VoterTableToCSV;
import com.voterverification.application.Entity.Users;
import com.voterverification.application.Entity.Voter;
import com.voterverification.application.repository.UsersRepoistory;

@Component
public class VoterToCSVProcessor implements ItemProcessor<Voter, VoterTableToCSV> {

    @Autowired
    private UsersRepoistory userRepository; // Assuming you have a User entity and repository

    @Override
    public VoterTableToCSV process(Voter voter) throws Exception {
    	VoterTableToCSV dto = new VoterTableToCSV();

        // Map fields from Voter to VoterDTO
        dto.setSerialNumber(voter.getSerialNumber());
        dto.setFullName(voter.getFullName());
        dto.setFatherName(voter.getFatherName());
        dto.setVoterAge(voter.getVoterAge());
        dto.setHouseNo(voter.getHouseNo());
        dto.setStreet(voter.getStreet());
        if(voter.getVerifiedTime()==null) {
        	dto.setVerifiedTime("NOT VOTED YET!!");
        }
        else {
        	dto.setVerifiedTime(voter.getVerifiedTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        
        dto.setWard(voter.getWard());
        dto.setVillage(voter.getVillage());
        if(!voter.isVoted()) {
        	dto.setVoted(false);
        }
        dto.setVoted(voter.isVoted());
        

        // Retrieve verifier name from UUID
        if (voter.getVerifiedBy() != null) {
            Users verifier = voter.getVerifiedBy();
            dto.setVerifiedBy(verifier.getUsername());
        } else {
            dto.setVerifiedBy("Not Voted");
        }

       

        return dto;
    }
}
