package com.voterverification.application.controller;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voterverification.application.exception.BatchServiceExceptionHandler;
import com.voterverification.application.util.ApiResponseHandler;

@RestController
@RequestMapping("/api/batch")
public class BatchController {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    @Qualifier(value = "importVoterJob")
    private Job importVoterJob;
    
    
    @Autowired
    @Qualifier(value = "exportVoterToCSVJob")
    private Job exportVoterToCSVJob;

    @PostMapping("/add-voters")
    public ResponseEntity<ApiResponseHandler> startBatchJob() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("startTime", System.currentTimeMillis())
                    .toJobParameters();
            jobLauncher.run(importVoterJob, jobParameters);
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponseHandler("Batch job started successfully!", HttpStatus.OK.value(), "Success"));
        } catch (Exception e) {
        	throw new BatchServiceExceptionHandler("Error starting batch job: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            
        }
    }
    
    @GetMapping("/get-CSV-export")
    public ResponseEntity<ApiResponseHandler> startBatchJobExport() {
    	 try {
             JobParameters jobParameters = new JobParametersBuilder()
                     .addLong("startTime", System.currentTimeMillis())
                     .toJobParameters();
             jobLauncher.run(exportVoterToCSVJob, jobParameters);
             return ResponseEntity.status(HttpStatus.OK).body(new ApiResponseHandler("Batch job started successfully!", HttpStatus.OK.value(), "Success"));
         } catch (Exception e) {
        	 throw new BatchServiceExceptionHandler("Error starting batch job: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
             
         }
     }
    }
