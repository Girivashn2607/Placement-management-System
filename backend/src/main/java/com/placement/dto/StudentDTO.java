package com.placement.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class StudentDTO {

    public static class ProfileResponse {
        private Long id;
        private Long userId;
        private String fullName;
        private String email;
        private String phoneNumber;
        private String registrationNumber;
        private String college;
        private String department;
        private String degree;
        private String specialization;
        private Integer graduationYear;
        private BigDecimal cgpa;
        private String preferredRoles;
        private String preferredLocations;
        private Integer profileCompletionPercentage;
        private List<SkillItem> skills;
        private List<ProjectItem> projects;
        private List<ExperienceItem> experiences;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
        public String getRegistrationNumber() { return registrationNumber; }
        public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }
        public String getCollege() { return college; }
        public void setCollege(String college) { this.college = college; }
        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }
        public String getDegree() { return degree; }
        public void setDegree(String degree) { this.degree = degree; }
        public String getSpecialization() { return specialization; }
        public void setSpecialization(String specialization) { this.specialization = specialization; }
        public Integer getGraduationYear() { return graduationYear; }
        public void setGraduationYear(Integer graduationYear) { this.graduationYear = graduationYear; }
        public BigDecimal getCgpa() { return cgpa; }
        public void setCgpa(BigDecimal cgpa) { this.cgpa = cgpa; }
        public String getPreferredRoles() { return preferredRoles; }
        public void setPreferredRoles(String preferredRoles) { this.preferredRoles = preferredRoles; }
        public String getPreferredLocations() { return preferredLocations; }
        public void setPreferredLocations(String preferredLocations) { this.preferredLocations = preferredLocations; }
        public Integer getProfileCompletionPercentage() { return profileCompletionPercentage; }
        public void setProfileCompletionPercentage(Integer profileCompletionPercentage) { this.profileCompletionPercentage = profileCompletionPercentage; }
        public List<SkillItem> getSkills() { return skills; }
        public void setSkills(List<SkillItem> skills) { this.skills = skills; }
        public List<ProjectItem> getProjects() { return projects; }
        public void setProjects(List<ProjectItem> projects) { this.projects = projects; }
        public List<ExperienceItem> getExperiences() { return experiences; }
        public void setExperiences(List<ExperienceItem> experiences) { this.experiences = experiences; }
    }

    public static class ProfileUpdateRequest {
        @NotBlank(message = "Registration number is required")
        private String registrationNumber;

        @NotBlank(message = "College name is required")
        private String college;

        @NotBlank(message = "Department is required")
        private String department;

        @NotBlank(message = "Degree is required")
        private String degree;

        private String specialization;

        @NotNull(message = "Graduation year is required")
        private Integer graduationYear;

        @NotNull(message = "CGPA is required")
        @DecimalMin(value = "0.00", message = "CGPA cannot be negative")
        @DecimalMax(value = "10.00", message = "CGPA cannot exceed 10.00")
        private BigDecimal cgpa;

        private String preferredRoles;
        private String preferredLocations;
        private String phoneNumber;

        private List<String> skills; // list of skill names
        private List<ProjectItem> projects;
        private List<ExperienceItem> experiences;

        public String getRegistrationNumber() { return registrationNumber; }
        public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }
        public String getCollege() { return college; }
        public void setCollege(String college) { this.college = college; }
        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }
        public String getDegree() { return degree; }
        public void setDegree(String degree) { this.degree = degree; }
        public String getSpecialization() { return specialization; }
        public void setSpecialization(String specialization) { this.specialization = specialization; }
        public Integer getGraduationYear() { return graduationYear; }
        public void setGraduationYear(Integer graduationYear) { this.graduationYear = graduationYear; }
        public BigDecimal getCgpa() { return cgpa; }
        public void setCgpa(BigDecimal cgpa) { this.cgpa = cgpa; }
        public String getPreferredRoles() { return preferredRoles; }
        public void setPreferredRoles(String preferredRoles) { this.preferredRoles = preferredRoles; }
        public String getPreferredLocations() { return preferredLocations; }
        public void setPreferredLocations(String preferredLocations) { this.preferredLocations = preferredLocations; }
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
        public List<String> getSkills() { return skills; }
        public void setSkills(List<String> skills) { this.skills = skills; }
        public List<ProjectItem> getProjects() { return projects; }
        public void setProjects(List<ProjectItem> projects) { this.projects = projects; }
        public List<ExperienceItem> getExperiences() { return experiences; }
        public void setExperiences(List<ExperienceItem> experiences) { this.experiences = experiences; }
    }

    public static class SkillItem {
        private Long id;
        private String name;
        private String proficiencyLevel;

        public SkillItem() {}
        public SkillItem(Long id, String name, String proficiencyLevel) {
            this.id = id;
            this.name = name;
            this.proficiencyLevel = proficiencyLevel;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getProficiencyLevel() { return proficiencyLevel; }
        public void setProficiencyLevel(String proficiencyLevel) { this.proficiencyLevel = proficiencyLevel; }
    }

    public static class ProjectItem {
        private Long id;
        private String title;
        private String description;
        private String technologies;
        private String projectUrl;
        private LocalDate startDate;
        private LocalDate endDate;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getTechnologies() { return technologies; }
        public void setTechnologies(String technologies) { this.technologies = technologies; }
        public String getProjectUrl() { return projectUrl; }
        public void setProjectUrl(String projectUrl) { this.projectUrl = projectUrl; }
        public LocalDate getStartDate() { return startDate; }
        public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
        public LocalDate getEndDate() { return endDate; }
        public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    }

    public static class ExperienceItem {
        private Long id;
        private String title;
        private String organization;
        private String experienceType;
        private String description;
        private LocalDate startDate;
        private LocalDate endDate;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getOrganization() { return organization; }
        public void setOrganization(String organization) { this.organization = organization; }
        public String getExperienceType() { return experienceType; }
        public void setExperienceType(String experienceType) { this.experienceType = experienceType; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public LocalDate getStartDate() { return startDate; }
        public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
        public LocalDate getEndDate() { return endDate; }
        public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    }
}
