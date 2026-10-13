package com.placement.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.placement.dto.ResumeDTO;
import com.placement.entity.Resume;
import com.placement.entity.ResumeAnalysis;
import com.placement.entity.StudentProfile;
import com.placement.repository.ResumeAnalysisRepository;
import com.placement.repository.ResumeRepository;
import com.placement.repository.StudentProfileRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final ResumeAnalysisRepository resumeAnalysisRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final PdfParserService pdfParserService;
    private final PdfGeneratorService pdfGeneratorService;
    private final AiService aiService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${app.storage.upload-dir:./uploads}")
    private String uploadDir;

    public ResumeService(ResumeRepository resumeRepository,
                         ResumeAnalysisRepository resumeAnalysisRepository,
                         StudentProfileRepository studentProfileRepository,
                         PdfParserService pdfParserService,
                         PdfGeneratorService pdfGeneratorService,
                         AiService aiService) {
        this.resumeRepository = resumeRepository;
        this.resumeAnalysisRepository = resumeAnalysisRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.pdfParserService = pdfParserService;
        this.pdfGeneratorService = pdfGeneratorService;
        this.aiService = aiService;
    }

    @Transactional
    public Resume saveResumeContent(Long studentUserId, ResumeDTO.SaveResumeRequest request) throws IOException {
        StudentProfile profile = studentProfileRepository.findByUserId(studentUserId)
                .orElseThrow(() -> new IllegalArgumentException("Student profile not found for user ID: " + studentUserId));

        Resume resume = new Resume();
        resume.setStudentProfile(profile);
        resume.setTitle(request.getTitle() != null ? request.getTitle() : "My Resume");
        resume.setBuilderGenerated(true);
        resume.setContentJson(objectMapper.writeValueAsString(request.getContent()));

        return resumeRepository.save(resume);
    }

    @Transactional
    public ResumeDTO.AnalysisResponse uploadAndAnalyzeResume(Long studentUserId, MultipartFile file, String targetGoal) throws IOException {
        StudentProfile profile = studentProfileRepository.findByUserId(studentUserId)
                .orElseThrow(() -> new IllegalArgumentException("Student profile not found for user ID: " + studentUserId));

        // Create upload directory safely
        Path uploadPath = Paths.get(uploadDir, "resumes");
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String safeFileName = UUID.randomUUID() + "_" + file.getOriginalFilename().replaceAll("[^a-zA-Z0-9.-]", "_");
        Path destination = uploadPath.resolve(safeFileName);
        file.transferTo(destination.toFile());

        // Extract text safely
        String extractedText = pdfParserService.extractTextFromPdf(file);

        // Save resume record
        Resume resume = new Resume();
        resume.setStudentProfile(profile);
        resume.setTitle(file.getOriginalFilename());
        resume.setFileName(safeFileName);
        resume.setFilePath(destination.toString());
        resume.setBuilderGenerated(false);
        resume = resumeRepository.save(resume);

        // Run AI analysis
        ResumeDTO.AnalysisResponse analysisResult = aiService.analyzeResume(extractedText, targetGoal);
        analysisResult.setResumeId(resume.getId());

        // Save analysis to DB
        ResumeAnalysis entity = new ResumeAnalysis();
        entity.setResume(resume);
        entity.setStudentProfile(profile);
        entity.setOverallScore(analysisResult.getOverallScore());
        entity.setSummaryFeedback(analysisResult.getQualitySummary());
        entity.setFormattingFeedback(analysisResult.getFormattingFeedback());
        entity.setGrammarFeedback(analysisResult.getGrammarFeedback());
        entity.setDetectedSkillsJson(objectMapper.writeValueAsString(analysisResult.getDetectedSkills()));
        entity.setMissingSkillsJson(objectMapper.writeValueAsString(analysisResult.getMissingSkills()));
        entity.setImprovementPointsJson(objectMapper.writeValueAsString(analysisResult.getImprovementPoints()));
        entity.setLearningRecommendationsJson(objectMapper.writeValueAsString(analysisResult.getLearningRecommendations()));

        entity = resumeAnalysisRepository.save(entity);
        analysisResult.setAnalysisId(entity.getId());

        return analysisResult;
    }

    public byte[] downloadResumePdf(Long resumeId, Long studentUserId) throws Exception {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new IllegalArgumentException("Resume not found with ID: " + resumeId));

        if (!resume.getStudentProfile().getUser().getId().equals(studentUserId)) {
            throw new SecurityException("Unauthorized: Cannot access this resume.");
        }

        if (resume.isBuilderGenerated() && resume.getContentJson() != null) {
            ResumeDTO.ResumeContent content = objectMapper.readValue(resume.getContentJson(), ResumeDTO.ResumeContent.class);
            return pdfGeneratorService.generateResumePdf(content);
        } else if (resume.getFilePath() != null) {
            File f = new File(resume.getFilePath());
            if (f.exists()) {
                return Files.readAllBytes(f.toPath());
            }
        }

        throw new IllegalArgumentException("Resume content or file not found.");
    }

    @Transactional(readOnly = true)
    public List<Resume> getStudentResumes(Long studentUserId) {
        StudentProfile profile = studentProfileRepository.findByUserId(studentUserId)
                .orElseThrow(() -> new IllegalArgumentException("Student profile not found"));
        return resumeRepository.findByStudentProfileIdOrderByCreatedAtDesc(profile.getId());
    }

    @Transactional(readOnly = true)
    public ResumeDTO.AnalysisResponse getLatestAnalysis(Long studentUserId) throws Exception {
        StudentProfile profile = studentProfileRepository.findByUserId(studentUserId)
                .orElseThrow(() -> new IllegalArgumentException("Student profile not found"));

        ResumeAnalysis analysis = resumeAnalysisRepository.findFirstByStudentProfileIdOrderByCreatedAtDesc(profile.getId())
                .orElse(null);

        if (analysis == null) return null;

        ResumeDTO.AnalysisResponse dto = new ResumeDTO.AnalysisResponse();
        dto.setAnalysisId(analysis.getId());
        dto.setResumeId(analysis.getResume().getId());
        dto.setOverallScore(analysis.getOverallScore());
        dto.setQualitySummary(analysis.getSummaryFeedback());
        dto.setFormattingFeedback(analysis.getFormattingFeedback());
        dto.setGrammarFeedback(analysis.getGrammarFeedback());

        if (analysis.getDetectedSkillsJson() != null) {
            dto.setDetectedSkills(objectMapper.readValue(analysis.getDetectedSkillsJson(), List.class));
        }
        if (analysis.getMissingSkillsJson() != null) {
            dto.setMissingSkills(objectMapper.readValue(analysis.getMissingSkillsJson(), List.class));
        }
        if (analysis.getImprovementPointsJson() != null) {
            dto.setImprovementPoints(objectMapper.readValue(analysis.getImprovementPointsJson(), List.class));
        }
        if (analysis.getLearningRecommendationsJson() != null) {
            dto.setLearningRecommendations(objectMapper.readValue(analysis.getLearningRecommendationsJson(), List.class));
        }

        return dto;
    }
}
