package com.voterverification.application.controller;



import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.voterverification.application.DTO.VoterDto;
import com.voterverification.application.repository.VoterRepository;
import com.voterverification.application.service.VoterService;
import com.voterverification.application.util.ApiResponseHandler;



@RestController
@RequestMapping("api/voter")
public class VoterController {

    private final VoterService voterService;
    
   private static final Logger LOGGER=  LoggerFactory.getLogger(VoterController.class);
   
    
    @Autowired
    private VoterRepository voterRepository;

    public VoterController(VoterService voterService) {
        this.voterService = voterService;
    }
    
    
    @GetMapping("/find/voter/{serialNumber}")
    @PreAuthorize("hasAnyRole('VERIFIER','ADMIN')")
    public ResponseEntity<ApiResponseHandler> voterBySerialNumber(@PathVariable int serialNumber){
    	ApiResponseHandler apiResponseHandler = voterService.findBySerialNumber(serialNumber);
    	return ResponseEntity.status(HttpStatus.FOUND).body(apiResponseHandler);	
    }
    
    @GetMapping("/voters")
    @PreAuthorize("hasAnyRole('ADMIN','VERIFIER')")
    public ResponseEntity<List<VoterDto>> getVoters(@RequestParam(value = "page", defaultValue = "0") int page,
                                 @RequestParam(value = "size", defaultValue = "25") int size) {
         List<VoterDto> voters = voterService.getVoters(page, size);
         return ResponseEntity.status(HttpStatus.OK).body(voters);
    }

    @PostMapping("/verify/{serialNumber}")
    @PreAuthorize("hasAnyRole('ADMIN','VERIFIER')")
    public ResponseEntity<ApiResponseHandler> verifyAndRegisterFingerprint(@PathVariable Integer serialNumber) {
    	LOGGER.info("GOT THE SERIAL NUMBER HERE IN CONTROLLER:"+serialNumber);
         ApiResponseHandler registerVoterFingerprint = voterService.registerVoterFingerprint(serialNumber);
         if(registerVoterFingerprint.getHttpStatus()==HttpStatus.CONFLICT.value()) {
        	 return ResponseEntity.status(HttpStatus.CONFLICT).body(registerVoterFingerprint);
         }
         return ResponseEntity.status(HttpStatus.OK).body(registerVoterFingerprint);
    }
    
    @GetMapping("/find/finger")
    @PreAuthorize("hasAnyRole('ADMIN','VERIFIER')")
    public ResponseEntity<ApiResponseHandler> findVoterByFinger(){
    	ApiResponseHandler voterByFinger = voterService.findVoterByFinger();
    	return ResponseEntity.status(HttpStatus.OK).body(voterByFinger);
    }
    
    
//    @PostMapping("/add")  //API NO LONGER USED
//    public ResponseEntity<ApiResponseHandler> addVoter(@RequestBody VoterDto voterDto){
//    	ApiResponseHandler voter = voterService.addVoter(voterDto);
//    	return ResponseEntity.status(HttpStatus.OK).body(voter);
//    }
    
    @GetMapping("/verified-voters-count")
    @PreAuthorize("hasAnyRole('ADMIN','VERIFIER')")
    public ResponseEntity<ApiResponseHandler> getVotersVerifiedByVerifierAndFraudCount() {
    	ApiResponseHandler votersVerifiedByVerifierAndFraudCount = voterService.getVotersVerifiedByVerifierAndFraudCount();
    	return ResponseEntity.status(HttpStatus.OK).body(votersVerifiedByVerifierAndFraudCount);
    }
}
