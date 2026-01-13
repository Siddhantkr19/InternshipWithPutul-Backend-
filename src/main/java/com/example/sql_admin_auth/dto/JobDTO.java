package com.example.sql_admin_auth.dto;

import lombok.Data;

@Data
public class JobDTO {
    private Long id;
    private String companyName;
    private String role;
    private String eligibility;
    private String salary;
    private String location;
    private String applyLink;
    private String duration;
    private String lastDateToApply;
}