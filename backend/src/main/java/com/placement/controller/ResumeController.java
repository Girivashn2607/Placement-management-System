package com.placement.controller;

import com.placement.dto.AiDTO;
import com.placement.dto.ApiResponse;
import com.placement.dto.ResumeDTO;
import com.placement.entity.Resume;
import com.placement.security.UserDetailsImpl;
import com.placement.service.AiService;
import com.placement.service.ResumeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/resumes")
@PreAuthorize("hasRole('STUDENT')")
public class ResumeController {

    private final ResumeService resumeService;
    private final AiService aiService;

    public ResumeController(ResumeService resumeService, AiService aiService) {
        this.resumeService = resumeService;
        this.aiService = aiService;
    }

    @PostMapping("/builder/save")
    public ResponseEntity<ApiResponse<Long>> saveBuilderResume(
            @AuthenticationPrincipal UserDetailsImpl principal,
            @Valid @RequestBody ResumeDTO.SaveResumeRequest request) throws Exception {
        Resume saved = resumeService.saveResumeContent(principal.getId(), request);
        return ResponseEntity.ok(ApiResponse.ok("Resume saved successfully", saved.getId()));
    }

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<ResumeDTO.AnalysisResponse>> uploadAndAnalyze(
            @AuthenticationPrincipal UserDetailsImpl principal,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "targetGoal", required = false) String targetGoal) throws Exception {
        ResumeDTO.AnalysisResponse result = resumeService.uploadAndAnalyzeResume(principal.getId(), file, targetGoal);
        return ResponseEntity.ok(ApiResponse.ok("Resume analyzed successfully", result));
    }

    @PostMapping("/ai/suggest")
    public ResponseEntity<ApiResponse<AiDTO.SuggestionResponse>> getAiSuggestion(
            @RequestBody AiDTO.SuggestionRequest request) {
        AiDTO.SuggestionResponse res = aiService.improveSummaryOrBullet(request.getContent(), request.getTargetRole());
        return ResponseEntity.ok(ApiResponse.ok("AI suggestion generated", res));
    }

    @GetMapping("/latest-analysis")
    public ResponseEntity<ApiResponse<ResumeDTO.AnalysisResponse>> getLatestAnalysis(
            @AuthenticationPrincipal UserDetailsImpl principal) throws Exception {
        ResumeDTO.AnalysisResponse analysis = resumeService.getLatestAnalysis(principal.getId());
        return ResponseEntity.ok(ApiResponse.ok("Latest analysis retrieved", analysis));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadPdf(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl principal) throws Exception {
        byte[] pdfBytes = resumeService.downloadResumePdf(id, principal.getId());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"resume_" + id + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @GetMapping("/my-resumes")
    public ResponseEntity<ApiResponse<List<Resume>>> getMyResumes(
            @AuthenticationPrincipal UserDetailsImpl principal) {
        List<Resume> list = resumeService.getStudentResumes(principal.getId());
        return ResponseEntity.ok(ApiResponse.ok("Resumes loaded", list));
    }
}
