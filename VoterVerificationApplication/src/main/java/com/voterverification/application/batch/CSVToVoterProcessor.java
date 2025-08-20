package com.voterverification.application.batch;

import java.security.SecureRandom;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.voterverification.application.DTO.CSVToVoterDto;
import com.voterverification.application.Entity.Users;
import com.voterverification.application.Entity.Voter;
import com.voterverification.application.exception.VoterExceptionHandler;
import com.voterverification.application.repository.VoterRepository;
import com.voterverification.application.util.UserPrincipalObject;

@Component
public class CSVToVoterProcessor implements ItemProcessor<CSVToVoterDto, Voter> {

    @Autowired
    private VoterRepository voterRepository;

    private final SecureRandom random = new SecureRandom();
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CSVToVoterBatchConfig.class);

    @Override
    public Voter process(CSVToVoterDto item) {
        LOGGER.info("Start processing VoterCSV with SerialNumber: {}", item.getSerialNumber());
        try {
            Optional<Voter> existingVoter = voterRepository.findBySerialNumber(item.getSerialNumber());
            LOGGER.info("Checking if Voter with SerialNumber: {} already exists", item.getSerialNumber());

            Voter voter = existingVoter.orElse(new Voter());
            if (existingVoter.isPresent()) {
                LOGGER.info("Existing voter found for SerialNumber: {}", item.getSerialNumber());
            } else {
                LOGGER.info("No existing voter found for SerialNumber: {}, creating a new Voter", item.getSerialNumber());
            }

            // Set the properties for the voter
            voter.setSerialNumber(item.getSerialNumber());
            voter.setFullName(item.getFullName());
            voter.setFatherName(item.getFatherName());
            voter.setVoterAge(item.getVoterAge());
            voter.setHouseNo(item.getHouseNo());
            voter.setStreet(item.getStreet());
            voter.setWard(item.getWard());
            voter.setVillage(item.getVillage());
            LOGGER.info("Set voter details for SerialNumber: {}", item.getSerialNumber());

            // Get the user and set the ElectionBoard
            Users user = UserPrincipalObject.getUser();
            if (user.getElectionBoard() == null) {
                LOGGER.warn("User's ElectionBoard is null for Voter with SerialNumber: {}", item.getSerialNumber());
            } else {
                voter.setElectionBoard(user.getElectionBoard());
                LOGGER.info("Set ElectionBoard for Voter with SerialNumber: {}", item.getSerialNumber());
            }

            // Generate a unique code if the voter is new
            if (!existingVoter.isPresent()) {
                voter.setUniqueCode(generateUniqueCode());
                LOGGER.info("Generated UniqueCode for new Voter with SerialNumber: {}", item.getSerialNumber());
            }

            LOGGER.info("Finished processing VoterCSV with SerialNumber: {}", item.getSerialNumber());
            return voter;

        } catch (Exception e) {
            LOGGER.error("Error processing VoterCSV with SerialNumber: {}", item.getSerialNumber(), e);
            throw new VoterExceptionHandler("Error processing VoterCSV with SerialNumber: " + item.getSerialNumber(), HttpStatus.BAD_REQUEST);
        }
    }
    

    private String generateUniqueCode() {
        String uniqueCode = String.format("%06d", random.nextInt(1000000));
        LOGGER.debug("Generated unique code: {}", uniqueCode);
        return uniqueCode;
    }
}
