package com.placement.controller;

import com.placement.dto.ApiResponse;
import com.placement.dto.StudentDTO;
import com.placement.entity.*;
import com.placement.repository.*;
import com.placement.security.UserDetailsImpl;
import com.placement.service.AdminReportService;
import com.placement.service.OpportunityService;
import com.placement.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminReportService adminReportService;
    private final StudentProfileRepository studentProfileRepository;
    private final StudentService studentService;
    private final CompanyRepository companyRepository;
    private final OpportunityRepository opportunityRepository;
    private final OpportunityService opportunityService;
    private final AnnouncementRepository announcementRepository;
    private final PlacementDriveRepository placementDriveRepository;
    private final UserRepository userRepository;

    public AdminController(AdminReportService adminReportService,
                           StudentProfileRepository studentProfileRepository,
                           StudentService studentService,
                           CompanyRepository companyRepository,
                           OpportunityRepository opportunityRepository,
                           OpportunityService opportunityService,
                           AnnouncementRepository announcementRepository,
                           PlacementDriveRepository placementDriveRepository,
                           UserRepository userRepository) {
        this.adminReportService = adminReportService;
        this.studentProfileRepository = studentProfileRepository;
        this.studentService = studentService;
        this.companyRepository = companyRepository;
        this.opportunityRepository = opportunityRepository;
        this.opportunityService = opportunityService;
        this.announcementRepository = announcementRepository;
        this.placementDriveRepository = placementDriveRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/overview")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboardOverview() {
        Map<String, Object> overview = adminReportService.getDashboardOverview();
        return ResponseEntity.ok(ApiResponse.ok("Overview stats loaded", overview));
    }

    @GetMapping("/students")
    public ResponseEntity<ApiResponse<Page<StudentDTO.ProfileResponse>>> getStudents(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) Integer graduationYear,
            @RequestParam(required = false) BigDecimal minCgpa,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        Page<StudentProfile> profiles = studentProfileRepository.findFilteredStudents(
                department, graduationYear, minCgpa, PageRequest.of(page, size, Sort.by("id").descending()));

        Page<StudentDTO.ProfileResponse> dtos = profiles.map(studentService::toDto);
        return ResponseEntity.ok(ApiResponse.ok("Students retrieved", dtos));
    }

    @GetMapping("/students/export-csv")
    public ResponseEntity<byte[]> exportStudentsCsv() {
        String csv = adminReportService.exportStudentsCsv();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"students_report.csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv.getBytes());
    }

    @GetMapping("/companies")
    public ResponseEntity<ApiResponse<List<Company>>> getCompanies() {
        return ResponseEntity.ok(ApiResponse.ok("Companies retrieved", companyRepository.findAll()));
    }

    @PostMapping("/companies")
    public ResponseEntity<ApiResponse<Company>> createCompany(@Valid @RequestBody Company company) {
        Company saved = companyRepository.save(company);
        return ResponseEntity.ok(ApiResponse.ok("Company created successfully", saved));
    }

    @GetMapping("/opportunities/pending")
    public ResponseEntity<ApiResponse<Page<Opportunity>>> getPendingOpportunities(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Opportunity> pending = opportunityRepository.findByStatus(
                OpportunityStatus.PENDING_APPROVAL, PageRequest.of(page, size, Sort.by("createdAt").descending()));
        return ResponseEntity.ok(ApiResponse.ok("Pending opportunities", pending));
    }

    @PatchMapping("/opportunities/{id}/status")
    public ResponseEntity<ApiResponse<String>> updateOpportunityStatus(
            @PathVariable Long id,
            @RequestParam OpportunityStatus status) {
        opportunityService.updateStatus(id, status);
        return ResponseEntity.ok(ApiResponse.ok("Opportunity status updated to " + status, null));
    }

    @PostMapping("/announcements")
    public ResponseEntity<ApiResponse<Announcement>> createAnnouncement(
            @AuthenticationPrincipal UserDetailsImpl principal,
            @RequestBody Announcement announcement) {
        User user = userRepository.findById(principal.getId()).orElse(null);
        announcement.setCreatedBy(user);
        Announcement saved = announcementRepository.save(announcement);
        return ResponseEntity.ok(ApiResponse.ok("Announcement posted", saved));
    }

    @GetMapping("/drives")
    public ResponseEntity<ApiResponse<List<PlacementDrive>>> getPlacementDrives() {
        return ResponseEntity.ok(ApiResponse.ok("Drives loaded", placementDriveRepository.findAllByOrderByDriveDateAsc()));
    }

    @PostMapping("/drives")
    public ResponseEntity<ApiResponse<PlacementDrive>> createPlacementDrive(@RequestBody PlacementDrive drive) {
        PlacementDrive saved = placementDriveRepository.save(drive);
        return ResponseEntity.ok(ApiResponse.ok("Placement drive scheduled", saved));
    }
}
