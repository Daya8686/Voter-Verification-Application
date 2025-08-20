package com.voterverification.application.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voterverification.application.DTO.CreateElectionBoardDto;
import com.voterverification.application.service.ElectionBoardService;
import com.voterverification.application.util.ApiResponseHandler;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class ElectionBoardController {
	
	@Autowired
	private ElectionBoardService electionBoardService;
	
	
	
	@PostMapping("/create-election-board")
	@PreAuthorize("hasAnyRole('ADMIN')")
	public ResponseEntity<ApiResponseHandler> createElectionBoard(@Valid @RequestBody CreateElectionBoardDto createElectionBoardDto){
		ApiResponseHandler electionBoard = electionBoardService.createElectionBoard(createElectionBoardDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(electionBoard);
	}
	
	@GetMapping("/election-board")
	public ResponseEntity<ApiResponseHandler> getElectionBoard(){
		ApiResponseHandler electionBoard = electionBoardService.getElectionBoard();
		return ResponseEntity.status(HttpStatus.OK).body(electionBoard);
	}
	
	@PutMapping("/election-board")
	@PreAuthorize("hasAnyRole('ADMIN')")
	public ResponseEntity<ApiResponseHandler> updateElectionBoard(@Valid @RequestBody CreateElectionBoardDto createElectionBoardDto){
		ApiResponseHandler updateEletionBoard = electionBoardService.updateEletionBoard( createElectionBoardDto);
		return ResponseEntity.status(HttpStatus.OK).body(updateEletionBoard);
	}
	
//	@DeleteMapping("/remove-election-board")
//	@PreAuthorize("hasAnyRole('ADMIN')")
//	public ResponseEntity<ApiResponseHandler> deleteElectionBoard(){
//		ApiResponseHandler deleteElectionBoard = electionBoardService.deleteElectionBoard();
//		return ResponseEntity.status(HttpStatus.OK).body(deleteElectionBoard);
//	}
	
	@PutMapping("/connect-to-electionBoard/{electionBoardCode}")
	public ResponseEntity<ApiResponseHandler> getConnectedToElectionBoardByCode(@PathVariable String electionBoardCode){
		ApiResponseHandler connectedToElectionBoardByCode = electionBoardService.getConnectedToElectionBoardByCode(electionBoardCode);
		return ResponseEntity.status(HttpStatus.OK).body(connectedToElectionBoardByCode);
	}

}
