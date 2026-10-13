package com.placement.controller;

import com.placement.dto.ApiResponse;
import com.placement.dto.OpportunityDTO;
import com.placement.dto.StudentDTO;
import com.placement.entity.Company;
import com.placement.entity.Opportunity;
import com.placement.entity.RecruiterProfile;
import com.placement.entity.StudentProfile;
import com.placement.repository.CompanyRepository;
import com.placement.repository.OpportunityRepository;
import com.placement.repository.RecruiterProfileRepository;
import com.placement.repository.StudentProfileRepository;
import com.placement.security.UserDetailsImpl;
import com.placement.service.OpportunityService;
import com.placement.service.StudentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recruiter")
@PreAuthorize("hasRole('RECRUITER')")
public class RecruiterController {

    private final RecruiterProfileRepository recruiterProfileRepository;
    private final CompanyRepository companyRepository;
    private final OpportunityRepository opportunityRepository;
    private final OpportunityService opportunityService;
    private final StudentProfileRepository studentProfileRepository;
    private final StudentService studentService;

    public RecruiterController(RecruiterProfileRepository recruiterProfileRepository,
                               CompanyRepository companyRepository,
                               OpportunityRepository opportunityRepository,
                               OpportunityService opportunityService,
                               StudentProfileRepository studentProfileRepository,
                               StudentService studentService) {
        this.recruiterProfileRepository = recruiterProfileRepository;
        this.companyRepository = companyRepository;
        this.opportunityRepository = opportunityRepository;
        this.opportunityService = opportunityService;
        this.studentProfileRepository = studentProfileRepository;
        this.studentService = studentService;
    }

    @GetMapping("/my-company")
    public ResponseEntity<ApiResponse<Company>> getMyCompany(@AuthenticationPrincipal UserDetailsImpl principal) {
        RecruiterProfile profile = recruiterProfileRepository.findByUserId(principal.getId())
                .orElseThrow(() -> new IllegalArgumentException("Recruiter profile not found"));
        return ResponseEntity.ok(ApiResponse.ok("Company loaded", profile.getCompany()));
    }

    @PutMapping("/my-company")
    public ResponseEntity<ApiResponse<Company>> updateCompany(
            @AuthenticationPrincipal UserDetailsImpl principal,
            @RequestBody Company update) {
        RecruiterProfile profile = recruiterProfileRepository.findByUserId(principal.getId())
                .orElseThrow(() -> new IllegalArgumentException("Recruiter profile not found"));

        Company c = profile.getCompany();
        if (update.getWebsite() != null) c.setWebsite(update.getWebsite());
        if (update.getIndustry() != null) c.setIndustry(update.getIndustry());
        if (update.getLocation() != null) c.setLocation(update.getLocation());
        if (update.getDescription() != null) c.setDescription(update.getDescription());
        if (update.getLogoUrl() != null) c.setLogoUrl(update.getLogoUrl());

        Company saved = companyRepository.save(c);
        return ResponseEntity.ok(ApiResponse.ok("Company updated", saved));
    }

    @GetMapping("/my-opportunities")
    public ResponseEntity<ApiResponse<Page<OpportunityDTO.OpportunityResponse>>> getMyOpportunities(
            @AuthenticationPrincipal UserDetailsImpl principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        RecruiterProfile profile = recruiterProfileRepository.findByUserId(principal.getId())
                .orElseThrow(() -> new IllegalArgumentException("Recruiter profile not found"));

        Page<Opportunity> opps = opportunityRepository.findByCompanyId(
                profile.getCompany().getId(), PageRequest.of(page, size, Sort.by("createdAt").descending()));

        Page<OpportunityDTO.OpportunityResponse> dtos = opps.map(opp -> opportunityService.toResponseDto(opp, null));
        return ResponseEntity.ok(ApiResponse.ok("Company opportunities loaded", dtos));
    }

    @GetMapping("/candidates")
    public ResponseEntity<ApiResponse<Page<StudentDTO.ProfileResponse>>> getEligibleCandidates(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<StudentProfile> students = studentProfileRepository.findAll(
                PageRequest.of(page, size, Sort.by("cgpa").descending()));
        return ResponseEntity.ok(ApiResponse.ok("Candidates loaded", students.map(studentService::toDto)));
    }
}
