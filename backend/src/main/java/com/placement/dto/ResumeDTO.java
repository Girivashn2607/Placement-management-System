package com.placement.dto;

import java.util.List;

public class ResumeDTO {

    public static class ResumeContent {
        private PersonalInfo personalInfo;
        private String summary;
        private List<EducationItem> education;
        private List<String> skills;
        private List<StudentDTO.ProjectItem> projects;
        private List<StudentDTO.ExperienceItem> experiences;
        private List<String> certifications;
        private List<String> achievements;
        private List<String> languages;

        public PersonalInfo getPersonalInfo() { return personalInfo; }
        public void setPersonalInfo(PersonalInfo personalInfo) { this.personalInfo = personalInfo; }
        public String getSummary() { return summary; }
        public void setSummary(String summary) { this.summary = summary; }
        public List<EducationItem> getEducation() { return education; }
        public void setEducation(List<EducationItem> education) { this.education = education; }
        public List<String> getSkills() { return skills; }
        public void setSkills(List<String> skills) { this.skills = skills; }
        public List<StudentDTO.ProjectItem> getProjects() { return projects; }
        public void setProjects(List<StudentDTO.ProjectItem> projects) { this.projects = projects; }
        public List<StudentDTO.ExperienceItem> getExperiences() { return experiences; }
        public void setExperiences(List<StudentDTO.ExperienceItem> experiences) { this.experiences = experiences; }
        public List<String> getCertifications() { return certifications; }
        public void setCertifications(List<String> certifications) { this.certifications = certifications; }
        public List<String> getAchievements() { return achievements; }
        public void setAchievements(List<String> achievements) { this.achievements = achievements; }
        public List<String> getLanguages() { return languages; }
        public void setLanguages(List<String> languages) { this.languages = languages; }
    }

    public static class PersonalInfo {
        private String fullName;
        private String email;
        private String phone;
        private String location;
        private String linkedin;
        private String github;
        private String portfolio;

        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public String getLinkedin() { return linkedin; }
        public void setLinkedin(String linkedin) { this.linkedin = linkedin; }
        public String getGithub() { return github; }
        public void setGithub(String github) { this.github = github; }
        public String getPortfolio() { return portfolio; }
        public void setPortfolio(String portfolio) { this.portfolio = portfolio; }
    }

    public static class EducationItem {
        private String institution;
        private String degree;
        private String fieldOfStudy;
        private Integer startYear;
        private Integer endYear;
        private String gradeOrCgpa;

        public String getInstitution() { return institution; }
        public void setInstitution(String institution) { this.institution = institution; }
        public String getDegree() { return degree; }
        public void setDegree(String degree) { this.degree = degree; }
        public String getFieldOfStudy() { return fieldOfStudy; }
        public void setFieldOfStudy(String fieldOfStudy) { this.fieldOfStudy = fieldOfStudy; }
        public Integer getStartYear() { return startYear; }
        public void setStartYear(Integer startYear) { this.startYear = startYear; }
        public Integer getEndYear() { return endYear; }
        public void setEndYear(Integer endYear) { this.endYear = endYear; }
        public String getGradeOrCgpa() { return gradeOrCgpa; }
        public void setGradeOrCgpa(String gradeOrCgpa) { this.gradeOrCgpa = gradeOrCgpa; }
    }

    public static class SaveResumeRequest {
        private String title;
        private ResumeContent content;
        private String template = "MODERN";

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public ResumeContent getContent() { return content; }
        public void setContent(ResumeContent content) { this.content = content; }
        public String getTemplate() { return template; }
        public void setTemplate(String template) { this.template = template; }
    }

    public static class AnalysisResponse {
        private Long analysisId;
        private Long resumeId;
        private int overallScore; // 0 - 100
        private String qualitySummary;
        private String formattingFeedback;
        private String grammarFeedback;
        private List<String> detectedSkills;
        private List<String> missingSkills;
        private List<String> improvementPoints;
        private List<String> learningRecommendations;
        private String advisoryDisclaimer = "AI analysis is advisory. Skill matching is an estimate, not a guarantee of placement.";

        public Long getAnalysisId() { return analysisId; }
        public void setAnalysisId(Long analysisId) { this.analysisId = analysisId; }
        public Long getResumeId() { return resumeId; }
        public void setResumeId(Long resumeId) { this.resumeId = resumeId; }
        public int getOverallScore() { return overallScore; }
        public void setOverallScore(int overallScore) { this.overallScore = overallScore; }
        public String getQualitySummary() { return qualitySummary; }
        public void setQualitySummary(String qualitySummary) { this.qualitySummary = qualitySummary; }
        public String getFormattingFeedback() { return formattingFeedback; }
        public void setFormattingFeedback(String formattingFeedback) { this.formattingFeedback = formattingFeedback; }
        public String getGrammarFeedback() { return grammarFeedback; }
        public void setGrammarFeedback(String grammarFeedback) { this.grammarFeedback = grammarFeedback; }
        public List<String> getDetectedSkills() { return detectedSkills; }
        public void setDetectedSkills(List<String> detectedSkills) { this.detectedSkills = detectedSkills; }
        public List<String> getMissingSkills() { return missingSkills; }
        public void setMissingSkills(List<String> missingSkills) { this.missingSkills = missingSkills; }
        public List<String> getImprovementPoints() { return improvementPoints; }
        public void setImprovementPoints(List<String> improvementPoints) { this.improvementPoints = improvementPoints; }
        public List<String> getLearningRecommendations() { return learningRecommendations; }
        public void setLearningRecommendations(List<String> learningRecommendations) { this.learningRecommendations = learningRecommendations; }
        public String getAdvisoryDisclaimer() { return advisoryDisclaimer; }
        public void setAdvisoryDisclaimer(String advisoryDisclaimer) { this.advisoryDisclaimer = advisoryDisclaimer; }
    }
}
