package com.placement.controller;

import com.placement.dto.ApiResponse;
import com.placement.dto.OpportunityDTO;
import com.placement.entity.Opportunity;
import com.placement.entity.Role;
import com.placement.entity.User;
import com.placement.repository.UserRepository;
import com.placement.security.UserDetailsImpl;
import com.placement.service.OpportunityService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/opportunities")
public class OpportunityController {

    private final OpportunityService opportunityService;
    private final UserRepository userRepository;

    public OpportunityController(OpportunityService opportunityService, UserRepository userRepository) {
        this.opportunityService = opportunityService;
        this.userRepository = userRepository;
    }

    @GetMapping("/public")
    public ResponseEntity<ApiResponse<Page<OpportunityDTO.OpportunityResponse>>> getPublicOpportunities(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<OpportunityDTO.OpportunityResponse> opps = opportunityService.getPublishedOpportunities(
                null, null, null, null,
                PageRequest.of(page, size, Sort.by("deadline").ascending()));
        return ResponseEntity.ok(ApiResponse.ok("Opportunities loaded", opps));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<OpportunityDTO.OpportunityResponse>>> getOpportunities(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String jobType,
            @RequestParam(required = false) String workMode,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetailsImpl principal) {

        Long studentUserId = null;
        if (principal != null) {
            studentUserId = principal.getId();
        }

        Page<OpportunityDTO.OpportunityResponse> opps = opportunityService.getPublishedOpportunities(
                keyword, jobType, workMode, studentUserId,
                PageRequest.of(page, size, Sort.by("deadline").ascending()));

        return ResponseEntity.ok(ApiResponse.ok("Opportunities loaded", opps));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OpportunityDTO.OpportunityResponse>> getOpportunityDetails(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl principal) {
        Long studentUserId = principal != null ? principal.getId() : null;
        OpportunityDTO.OpportunityResponse opp = opportunityService.getOpportunityById(id, studentUserId);
        return ResponseEntity.ok(ApiResponse.ok("Opportunity details", opp));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECRUITER')")
    public ResponseEntity<ApiResponse<String>> createOpportunity(
            @AuthenticationPrincipal UserDetailsImpl principal,
            @Valid @RequestBody OpportunityDTO.OpportunityRequest request) {
        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        boolean isAdmin = user.getRole() == Role.ADMIN;
        Opportunity opp = opportunityService.createOpportunity(request, user, isAdmin);

        String message = isAdmin ? "Opportunity published successfully." :
                "Opportunity submitted and pending administrator approval.";

        return ResponseEntity.ok(ApiResponse.ok(message, "ID: " + opp.getId()));
    }
}
