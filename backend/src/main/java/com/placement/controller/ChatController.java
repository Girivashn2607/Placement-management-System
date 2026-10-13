package com.placement.controller;

import com.placement.dto.AiDTO;
import com.placement.dto.ApiResponse;
import com.placement.entity.StudentProfile;
import com.placement.repository.StudentProfileRepository;
import com.placement.security.UserDetailsImpl;
import com.placement.service.AiService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final AiService aiService;
    private final StudentProfileRepository studentProfileRepository;

    public ChatController(AiService aiService, StudentProfileRepository studentProfileRepository) {
        this.aiService = aiService;
        this.studentProfileRepository = studentProfileRepository;
    }

    @PostMapping("/teximo")
    public ResponseEntity<ApiResponse<AiDTO.ChatResponse>> chatWithTeximo(
            @AuthenticationPrincipal UserDetailsImpl principal,
            @Valid @RequestBody AiDTO.ChatRequest request) {

        String studentContext = null;
        if (principal != null) {
            StudentProfile profile = studentProfileRepository.findByUserId(principal.getId()).orElse(null);
            if (profile != null) {
                studentContext = String.format("Student: %s, Dept: %s, Degree: %s, CGPA: %.2f, Grad Year: %d",
                        principal.getFullName(), profile.getDepartment(), profile.getDegree(),
                        profile.getCgpa(), profile.getGraduationYear());
            } else {
                studentContext = "User: " + principal.getFullName();
            }
        }

        AiDTO.ChatResponse response = aiService.chatWithTeximo(request.getMessage(), studentContext);
        return ResponseEntity.ok(ApiResponse.ok("Response received", response));
    }
}
