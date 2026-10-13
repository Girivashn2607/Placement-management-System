package com.placement.controller;

import com.placement.dto.ApiResponse;
import com.placement.dto.ComplaintDTO;
import com.placement.entity.ComplaintStatus;
import com.placement.security.UserDetailsImpl;
import com.placement.service.ComplaintService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/complaints")
public class ComplaintController {

    private final ComplaintService complaintService;

    public ComplaintController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<ComplaintDTO.ComplaintResponse>> submitComplaint(
            @AuthenticationPrincipal UserDetailsImpl principal,
            @Valid @RequestBody ComplaintDTO.ComplaintRequest request) {
        ComplaintDTO.ComplaintResponse res = complaintService.submitComplaint(principal.getId(), request);
        return ResponseEntity.ok(ApiResponse.ok("Complaint registered successfully", res));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<List<ComplaintDTO.ComplaintResponse>>> getMyComplaints(
            @AuthenticationPrincipal UserDetailsImpl principal) {
        List<ComplaintDTO.ComplaintResponse> list = complaintService.getStudentComplaints(principal.getId());
        return ResponseEntity.ok(ApiResponse.ok("Complaints fetched", list));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<ComplaintDTO.ComplaintResponse>>> getAllComplaints(
            @RequestParam(required = false) ComplaintStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ComplaintDTO.ComplaintResponse> p = complaintService.getAllComplaints(
                status, PageRequest.of(page, size, Sort.by("createdAt").descending()));
        return ResponseEntity.ok(ApiResponse.ok("Complaints retrieved", p));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ComplaintDTO.ComplaintResponse>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody ComplaintDTO.StatusUpdateRequest request) {
        ComplaintDTO.ComplaintResponse updated = complaintService.updateComplaintStatus(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Complaint status updated", updated));
    }
}
