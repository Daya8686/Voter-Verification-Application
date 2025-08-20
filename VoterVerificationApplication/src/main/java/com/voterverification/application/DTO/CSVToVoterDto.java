package com.voterverification.application.DTO;

import lombok.Data;

@Data
public class CSVToVoterDto {

    private Integer serialNumber;  // S.No from CSV
    private String fullName;       // Name of the Voter
    private String fatherName;     // Father's Name
    private Integer voterAge;      // Age
    private String houseNo;        // H.No
    private String street;         // Street
    private String ward;           // Ward
    private String village;        // VILLAGE
}
