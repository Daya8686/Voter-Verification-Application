package com.voterverification.application.service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.voterverification.application.DTO.AllUsersResponseDto;
import com.voterverification.application.DTO.CreateElectionBoardDto;
import com.voterverification.application.DTO.ElectionBoardDto;
import com.voterverification.application.Entity.ElectionBoard;
import com.voterverification.application.Entity.Users;
import com.voterverification.application.exception.ElectionBoardExceptionHandler;
import com.voterverification.application.repository.ElectionBoardRepository;
import com.voterverification.application.repository.UsersRepoistory;
import com.voterverification.application.util.ApiResponseHandler;
import com.voterverification.application.util.UserPrincipalObject;

import jakarta.transaction.Transactional;

@Service
public class ElectionBoardService {
	
	@Autowired
	private ElectionBoardRepository electionBoardRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private UsersRepoistory usersRepoistory;
	
	// Define the length of the organization code
    private static final int ELECTION_BOARD_CODE_LENGTH = 6;

    // Character set for the organization code (Uppercase letters and digits)
    private static final String CHARSET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private final SecureRandom random = new SecureRandom();
    
	
	private static final Logger logger = Logger.getLogger(ElectionBoardService.class.getName());

	@Transactional
	@Modifying
	public ApiResponseHandler createElectionBoard(CreateElectionBoardDto createElectionBoardDto) {
		Users user = UserPrincipalObject.getUser();
		if(user.getElectionBoard()!=null) {
			throw new ElectionBoardExceptionHandler("You are already assocated with one election board, cannot create another", HttpStatus.CONFLICT);
		}
		ElectionBoard electionBoard = modelMapper.map(createElectionBoardDto, ElectionBoard.class);
		electionBoard.setElectionBoardCode(generateUniqueElectionBoardCode());
		electionBoard.setElectionDay(electionBoard.getElectionDate().getDayOfWeek().toString());
		ElectionBoard savedElectionBoard = electionBoardRepository.save(electionBoard);
		
		user.setElectionBoard(savedElectionBoard);
		usersRepoistory.save(user);
		ElectionBoardDto electionBoardDto = modelMapper.map(savedElectionBoard, ElectionBoardDto.class);
		return new ApiResponseHandler(electionBoardDto, HttpStatus.CREATED.value(), "Success");
		
		
	}
	@Transactional
	public ApiResponseHandler getElectionBoard() {
		Users user = UserPrincipalObject.getUser();
		ElectionBoard electionBoard = user.getElectionBoard();
		if(electionBoard==null) {
			throw new ElectionBoardExceptionHandler("No Election board is created!!", HttpStatus.BAD_REQUEST);
		}
		ElectionBoardDto electionBoardDto = modelMapper.map(electionBoard, ElectionBoardDto.class);
		
		return new ApiResponseHandler(electionBoardDto, HttpStatus.OK.value(), "Success");
	}

	
	@Transactional
    @Modifying
	public ApiResponseHandler updateEletionBoard( CreateElectionBoardDto createElectionBoardDto) {
		
		ElectionBoard electionBoard = UserPrincipalObject.getUser().getElectionBoard();
		electionBoard.setElectionAddress(createElectionBoardDto.getElectionAddress());
		electionBoard.setElectionDate(createElectionBoardDto.getElectionDate());
		electionBoard.setElectionName(createElectionBoardDto.getElectionName());
		
		ElectionBoard savedElectionBoard = electionBoardRepository.save(electionBoard);
		ElectionBoardDto boardDto = modelMapper.map(savedElectionBoard, ElectionBoardDto.class);
		return new ApiResponseHandler(boardDto, HttpStatus.OK.value(), "Success");
		
	}
	
	@Transactional
	@Modifying
	public ApiResponseHandler deleteElectionBoard() {
		Users user = UserPrincipalObject.getUser();
		ElectionBoard electionBoard = user.getElectionBoard();
		electionBoardRepository.delete(electionBoard);
		return new ApiResponseHandler("Deleted Election Board!!", HttpStatus.OK.value(), "Success");
	}
	
	
	
	 private String generateUniqueElectionBoardCode() {
	        String code = "";
	        boolean isUnique = false;

	        while (!isUnique) {
	            // Generate a new random code
	            code = generateRandomCode();

	            // Check if this code already exists in the database
	            isUnique = electionBoardRepository.existsByElectionBoardCode(code);
	            
	            isUnique=isUnique?false:true;

	            if (!isUnique) {
	                logger.info("Generated code " + code + " already exists. Retrying...");
	            }
	        }

	        logger.info("Generated unique election board code: " + code);
	        return code;
	    }

	    /**
	     * Generates a random 6-character alphanumeric code with a mix of digits and letters.
	     */
	    private String generateRandomCode() {
	        List<Character> codeChars = new ArrayList(ELECTION_BOARD_CODE_LENGTH);

	        // Generate 6 random characters from the charset (both digits and letters)
	        for (int i = 0; i < ELECTION_BOARD_CODE_LENGTH; i++) {
	            int randomIndex = random.nextInt(CHARSET.length());
	            codeChars.add(CHARSET.charAt(randomIndex));
	        }

	        // Shuffle the list to make sure the characters are randomly ordered
	        Collections.shuffle(codeChars, random);

	        // Convert list back to string
	        StringBuilder code = new StringBuilder();
	        for (Character c : codeChars) {
	            code.append(c);
	        }

	        return "EB-"+code.toString();
	    }
	    @Transactional
	    @Modifying
		public ApiResponseHandler getConnectedToElectionBoardByCode(String electionBoardCode) {
			ElectionBoard byElectionBoardCode = electionBoardRepository.findByElectionBoardCode(electionBoardCode);
			if(byElectionBoardCode==null) {
				throw new ElectionBoardExceptionHandler("Invalid Election board code!!", HttpStatus.BAD_REQUEST);
				
			}
			Users user = UserPrincipalObject.getUser();
			user.setElectionBoard(byElectionBoardCode);
			Users savedUser = usersRepoistory.save(user);
			AllUsersResponseDto usersResponseDto = modelMapper.map(savedUser, AllUsersResponseDto.class);
			return new ApiResponseHandler(usersResponseDto, HttpStatus.OK.value(), "Success");
			
			
		}
	    

		
	

}
