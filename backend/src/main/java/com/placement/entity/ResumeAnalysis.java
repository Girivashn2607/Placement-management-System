package com.placement.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "resume_analyses")
public class ResumeAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_profile_id", nullable = false)
    private StudentProfile studentProfile;

    @Column(name = "overall_score")
    private Integer overallScore;

    @Column(name = "summary_feedback", columnDefinition = "TEXT")
    private String summaryFeedback;

    @Column(name = "formatting_feedback", columnDefinition = "TEXT")
    private String formattingFeedback;

    @Column(name = "grammar_feedback", columnDefinition = "TEXT")
    private String grammarFeedback;

    @Column(name = "detected_skills_json", columnDefinition = "TEXT")
    private String detectedSkillsJson;

    @Column(name = "missing_skills_json", columnDefinition = "TEXT")
    private String missingSkillsJson;

    @Column(name = "improvement_points_json", columnDefinition = "TEXT")
    private String improvementPointsJson;

    @Column(name = "learning_recommendations_json", columnDefinition = "TEXT")
    private String learningRecommendationsJson;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public ResumeAnalysis() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Resume getResume() { return resume; }
    public void setResume(Resume resume) { this.resume = resume; }

    public StudentProfile getStudentProfile() { return studentProfile; }
    public void setStudentProfile(StudentProfile studentProfile) { this.studentProfile = studentProfile; }

    public Integer getOverallScore() { return overallScore; }
    public void setOverallScore(Integer overallScore) { this.overallScore = overallScore; }

    public String getSummaryFeedback() { return summaryFeedback; }
    public void setSummaryFeedback(String summaryFeedback) { this.summaryFeedback = summaryFeedback; }

    public String getFormattingFeedback() { return formattingFeedback; }
    public void setFormattingFeedback(String formattingFeedback) { this.formattingFeedback = formattingFeedback; }

    public String getGrammarFeedback() { return grammarFeedback; }
    public void setGrammarFeedback(String grammarFeedback) { this.grammarFeedback = grammarFeedback; }

    public String getDetectedSkillsJson() { return detectedSkillsJson; }
    public void setDetectedSkillsJson(String detectedSkillsJson) { this.detectedSkillsJson = detectedSkillsJson; }

    public String getMissingSkillsJson() { return missingSkillsJson; }
    public void setMissingSkillsJson(String missingSkillsJson) { this.missingSkillsJson = missingSkillsJson; }

    public String getImprovementPointsJson() { return improvementPointsJson; }
    public void setImprovementPointsJson(String improvementPointsJson) { this.improvementPointsJson = improvementPointsJson; }

    public String getLearningRecommendationsJson() { return learningRecommendationsJson; }
    public void setLearningRecommendationsJson(String learningRecommendationsJson) { this.learningRecommendationsJson = learningRecommendationsJson; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
