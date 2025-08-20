package com.voterverification.application.service;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.voterverification.application.DTO.AllUsersResponseDto;
import com.voterverification.application.DTO.ElectionBoardDto;
import com.voterverification.application.DTO.VoterDto;
import com.voterverification.application.DTO.VoterVotedSuccessDto;
import com.voterverification.application.DTO.VotersVerifiedAndFraudDetectionCount;
import com.voterverification.application.DTO.VotersVerifiedDto;
import com.voterverification.application.Entity.ElectionBoard;
import com.voterverification.application.Entity.Users;
import com.voterverification.application.Entity.Voter;
import com.voterverification.application.exception.VoterExceptionHandler;
import com.voterverification.application.repository.UsersRepoistory;
import com.voterverification.application.repository.VoterRepository;
import com.voterverification.application.util.ApiResponseHandler;
import com.voterverification.application.util.UserPrincipalObject;

import jakarta.transaction.Transactional;

@Service
public class VoterService {

    private final VoterRepository voterRepository;
    private final FingerprintService fingerprintService;
    
    @Autowired
    private ModelMapper modelMapper;
    
    @Autowired
    private UsersRepoistory usersRepoistory;
    
    private static final Logger LOGGER= LoggerFactory.getLogger(VoterService.class);

    public VoterService(VoterRepository voterRepository, FingerprintService fingerprintService ) {
        this.voterRepository = voterRepository;
        this.fingerprintService = fingerprintService;
    }

    @Transactional
    public ApiResponseHandler registerVoterFingerprint(Integer serialNumber) {
    	 Users user = UserPrincipalObject.getUser();
    	LOGGER.info("SERIAL NUMBER IS IN SERVICE:"+serialNumber);
        Optional<Voter> voterOpt = voterRepository.findBySerialNumber(serialNumber);
        LOGGER.info("VOTER IS FOUND FROM OPTIONAL CLASS");
        if (voterOpt.isEmpty()) {
        	LOGGER.info("VOTER IS NOT FOUND!!");
        	throw new VoterExceptionHandler("Voter is not in DataBase", HttpStatus.CONFLICT);
        }

        Voter voter = voterOpt.get();
        if (voter.isVoted()) {
        	LOGGER.info("VOTER IS ALREADY VOTED!");
        	throw new VoterExceptionHandler("Voter has already voted!", HttpStatus.CONFLICT);
        }

        byte[] newFingerprintTemplate = fingerprintService.captureFingerprintTemplate();

        List<Voter> votedUsers = voterRepository.findByIsVotedTrue();
        for (Voter votedVoter : votedUsers) {
            if (fingerprintService.matchFingerprints(votedVoter.getFingerprintTemplate(), newFingerprintTemplate)) {
            	long fraudDetectedCountByUser = user.getFraudDetectedCountByUser();
            	System.out.println(fraudDetectedCountByUser);
            	user.setFraudDetectedCountByUser(fraudDetectedCountByUser+1);
            	System.out.println("Value incrememted");
            	Users savedUser = usersRepoistory.save(user);
            	System.out.println(savedUser.getFraudDetectedCountByUser());
            	 return new ApiResponseHandler("Fraud Detected: You have already voted! as "+votedVoter.getFullName()+" S/O : "+votedVoter.getFatherName()+" and your age is "+votedVoter.getVoterAge()+" and you are from "+votedVoter.getVillage()+". And Verified by: "+votedVoter.getVerifiedBy().getUsername(), HttpStatus.CONFLICT.value(),"FRAUD ALERT!!!");
            }
        }

       
        voter.setFingerprintTemplate(newFingerprintTemplate);
        voter.setVoted(true);
        voter.setVerified(true);
        voter.setVerifiedTime(LocalDateTime.now());
        voter.setVerifiedBy(user);
        long verifiedVotersCount = user.getVerifiedVotersCount();
        user.setVerifiedVotersCount(verifiedVotersCount+1);
        usersRepoistory.save(user);
        Voter savedVoter = voterRepository.save(voter);
        VoterVotedSuccessDto voterVotedSuccessDto = modelMapper.map(savedVoter, VoterVotedSuccessDto.class);
		  voterVotedSuccessDto.setElectionBoardName(savedVoter.getElectionBoard().getElectionName());
        return new ApiResponseHandler(voterVotedSuccessDto, HttpStatus.OK.value(), "Voter Successfully Voted!!");
        //Success message with Voted=True, VerifiedBy=User Name, with ElectionBoard name, and Voter details
    }

//    @Transactional  //NO LONGER NEEDED THIS METHOD
//    @Modifying
//	public ApiResponseHandler addVoter(VoterDto voterDto) {
//		Voter voter = modelMapper.map(voterDto, Voter.class);
//    	Users user = UserPrincipalObject.getUser();
//    	ElectionBoard electionBoard = user.getElectionBoard();
//    	voter.setElectionBoard(electionBoard);
//    	Voter savedVoter = voterRepository.save(voter);
//    	VoterDto voterDto2 = modelMapper.map(savedVoter, VoterDto.class);
//    	return new ApiResponseHandler(voterDto2, HttpStatus.CREATED.value(), "Success");
//    	
//    	
//		
//	}

    @Transactional
	public ApiResponseHandler findVoterByFinger() {
		
		 byte[] newFingerprintTemplate = fingerprintService.captureFingerprintTemplate();

	        List<Voter> votedUsers = voterRepository.findByIsVotedTrue();
	        for (Voter votedVoter : votedUsers) {
	            if (fingerprintService.matchFingerprints(votedVoter.getFingerprintTemplate(), newFingerprintTemplate)) {
	            	VoterVotedSuccessDto voterVotedSuccessDto = modelMapper.map(votedVoter, VoterVotedSuccessDto.class);
	            	Users verifiedBy = votedVoter.getVerifiedBy();
	            	AllUsersResponseDto allUsersResponseDto = modelMapper.map(verifiedBy, AllUsersResponseDto.class);
	            	ElectionBoard electionBoard = verifiedBy.getElectionBoard();
	            	ElectionBoardDto electionBoardDto = modelMapper.map(electionBoard, ElectionBoardDto.class);
	            	allUsersResponseDto.setElectionBoardDto(electionBoardDto);
	            	voterVotedSuccessDto.setVerifiedBy(allUsersResponseDto);
    				voterVotedSuccessDto.setElectionBoardName(votedVoter.getElectionBoard().getElectionName());
	            	return  new ApiResponseHandler(voterVotedSuccessDto, HttpStatus.FOUND.value(), "Voter already voted!!!");
	            }
	        }
	        return new ApiResponseHandler("No match found!!!", HttpStatus.OK.value(), "Clear finger print!");
		
	}

    @Transactional
	public ApiResponseHandler findBySerialNumber(int serialNumber) {
		Optional<Voter> voterBySerialNumber = voterRepository.findBySerialNumber(serialNumber);
		if(voterBySerialNumber.isEmpty()) {
			throw new VoterExceptionHandler("Voter not listed in voter list", HttpStatus.NOT_FOUND);
		}
		Voter voter = voterBySerialNumber.get();
		VoterVotedSuccessDto voterVotedSuccessDto = modelMapper.map(voter, VoterVotedSuccessDto.class);
		voterVotedSuccessDto.setElectionBoardName(voter.getElectionBoard().getElectionName());
		return new ApiResponseHandler(voterVotedSuccessDto, HttpStatus.OK.value(), "Found Voter");
		
	}

    @Transactional
	public List<VoterDto> getVoters(int pageNumber, int size) {
		Pageable pageable = PageRequest.of(pageNumber, size, Sort.by(Sort.Order.asc("serialNumber")));
       Page<Voter> allVoters = voterRepository.findAll(pageable);
       List<VoterDto> allVotersCollection = allVoters.stream().map(voter->{
    	   VoterDto voterDto = modelMapper.map(voter, VoterDto.class);
    	   return voterDto;
       }).collect(Collectors.toList());
       return allVotersCollection;
	}

    @Transactional
	public ApiResponseHandler getVotersVerifiedByVerifierAndFraudCount() {
		Users user = UserPrincipalObject.getUser();
		List<Voter> listOfVoters = voterRepository.findByVerifiedBy(user);
		long verifiedVotersCount = user.getVerifiedVotersCount();
		if(verifiedVotersCount!=(long)listOfVoters.size()) {
			LOGGER.info("ISSUE IN COUNT OF VOTERS VERIFIED BY: "+user.getUsername()+" WITH DIFFERENCE "+verifiedVotersCount+" ---"+listOfVoters.size());
		}
		long fraudDetectedCountByUser = user.getFraudDetectedCountByUser();
		List<VotersVerifiedDto> collectedVoters = listOfVoters.stream().map(voter->{
			VotersVerifiedDto votersVerifiedDto = modelMapper.map(voter, VotersVerifiedDto.class);
			return votersVerifiedDto;
		}).collect(Collectors.toList());
		
		
		VotersVerifiedAndFraudDetectionCount votersVerifiedAndFraudDetectionCount= new VotersVerifiedAndFraudDetectionCount();
		votersVerifiedAndFraudDetectionCount.setVerifiedVoters(verifiedVotersCount);
		votersVerifiedAndFraudDetectionCount.setFraudDetectedCount(fraudDetectedCountByUser);
		votersVerifiedAndFraudDetectionCount.setVerifiedAndVoterVoters(collectedVoters);
		return new ApiResponseHandler(votersVerifiedAndFraudDetectionCount, HttpStatus.OK.value(), "Success");
		
	}
}
