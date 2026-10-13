package com.placement.controller;

import com.placement.dto.ApiResponse;
import com.placement.dto.StudentDTO;
import com.placement.entity.Opportunity;
import com.placement.entity.OpportunityStatus;
import com.placement.entity.StudentProfile;
import com.placement.repository.AnnouncementRepository;
import com.placement.repository.NotificationRepository;
import com.placement.repository.OpportunityRepository;
import com.placement.repository.StudentProfileRepository;
import com.placement.security.UserDetailsImpl;
import com.placement.service.EligibilityService;
import com.placement.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/student")
@PreAuthorize("hasRole('STUDENT')")
public class StudentController {

    private final StudentService studentService;
    private final StudentProfileRepository studentProfileRepository;
    private final OpportunityRepository opportunityRepository;
    private final EligibilityService eligibilityService;
    private final AnnouncementRepository announcementRepository;
    private final NotificationRepository notificationRepository;

    public StudentController(StudentService studentService,
                             StudentProfileRepository studentProfileRepository,
                             OpportunityRepository opportunityRepository,
                             EligibilityService eligibilityService,
                             AnnouncementRepository announcementRepository,
                             NotificationRepository notificationRepository) {
        this.studentService = studentService;
        this.studentProfileRepository = studentProfileRepository;
        this.opportunityRepository = opportunityRepository;
        this.eligibilityService = eligibilityService;
        this.announcementRepository = announcementRepository;
        this.notificationRepository = notificationRepository;
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<StudentDTO.ProfileResponse>> getProfile(
            @AuthenticationPrincipal UserDetailsImpl principal) {
        StudentDTO.ProfileResponse profile = studentService.getProfileByUserId(principal.getId());
        return ResponseEntity.ok(ApiResponse.ok("Profile fetched", profile));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<StudentDTO.ProfileResponse>> updateProfile(
            @AuthenticationPrincipal UserDetailsImpl principal,
            @Valid @RequestBody StudentDTO.ProfileUpdateRequest request) {
        StudentDTO.ProfileResponse updated = studentService.updateProfile(principal.getId(), request);
        return ResponseEntity.ok(ApiResponse.ok("Profile updated successfully", updated));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboardOverview(
            @AuthenticationPrincipal UserDetailsImpl principal) {
        StudentProfile profile = studentProfileRepository.findByUserId(principal.getId()).orElse(null);

        List<Opportunity> published = opportunityRepository.findByStatus(OpportunityStatus.PUBLISHED);
        long matchingCount = 0;

        if (profile != null) {
            for (Opportunity opp : published) {
                var el = eligibilityService.evaluateEligibility(profile, opp);
                if (el.isEligible()) {
                    matchingCount++;
                }
            }
        }

        Map<String, Object> data = new HashMap<>();
        data.put("studentName", principal.getFullName());
        data.put("profileCompletionPercentage", profile != null ? profile.getProfileCompletionPercentage() : 0);
        data.put("availableOpportunitiesCount", published.size());
        data.put("matchingOpportunitiesCount", matchingCount);
        data.put("recentAnnouncements", announcementRepository.findAllByOrderByCreatedAtDesc().stream().limit(5).toList());
        data.put("unreadNotificationsCount", notificationRepository.countByUserIdAndReadFalse(principal.getId()));

        return ResponseEntity.ok(ApiResponse.ok("Dashboard data loaded", data));
    }
}
