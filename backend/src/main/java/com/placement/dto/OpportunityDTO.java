package com.placement.dto;

import com.placement.entity.JobType;
import com.placement.entity.OpportunityStatus;
import com.placement.entity.WorkMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class OpportunityDTO {

    public static class OpportunityRequest {
        @NotNull(message = "Company ID is required")
        private Long companyId;

        @NotBlank(message = "Title is required")
        private String title;

        @NotBlank(message = "Description is required")
        private String description;

        @NotNull(message = "Job type is required")
        private JobType jobType;

        @NotNull(message = "Work mode is required")
        private WorkMode workMode;

        private String location;
        private BigDecimal minCgpa;
        private String allowedDegrees;
        private Integer targetGraduationYear;
        private String allowedDepartments;
        private String salaryOrStipend;
        private String experienceRequirements;
        private String recruiterContact;
        private String externalApplyUrl;

        @NotNull(message = "Deadline is required")
        private LocalDate deadline;

        private List<String> mandatorySkills;
        private List<String> optionalSkills;

        public Long getCompanyId() { return companyId; }
        public void setCompanyId(Long companyId) { this.companyId = companyId; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public JobType getJobType() { return jobType; }
        public void setJobType(JobType jobType) { this.jobType = jobType; }
        public WorkMode getWorkMode() { return workMode; }
        public void setWorkMode(WorkMode workMode) { this.workMode = workMode; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public BigDecimal getMinCgpa() { return minCgpa; }
        public void setMinCgpa(BigDecimal minCgpa) { this.minCgpa = minCgpa; }
        public String getAllowedDegrees() { return allowedDegrees; }
        public void setAllowedDegrees(String allowedDegrees) { this.allowedDegrees = allowedDegrees; }
        public Integer getTargetGraduationYear() { return targetGraduationYear; }
        public void setTargetGraduationYear(Integer targetGraduationYear) { this.targetGraduationYear = targetGraduationYear; }
        public String getAllowedDepartments() { return allowedDepartments; }
        public void setAllowedDepartments(String allowedDepartments) { this.allowedDepartments = allowedDepartments; }
        public String getSalaryOrStipend() { return salaryOrStipend; }
        public void setSalaryOrStipend(String salaryOrStipend) { this.salaryOrStipend = salaryOrStipend; }
        public String getExperienceRequirements() { return experienceRequirements; }
        public void setExperienceRequirements(String experienceRequirements) { this.experienceRequirements = experienceRequirements; }
        public String getRecruiterContact() { return recruiterContact; }
        public void setRecruiterContact(String recruiterContact) { this.recruiterContact = recruiterContact; }
        public String getExternalApplyUrl() { return externalApplyUrl; }
        public void setExternalApplyUrl(String externalApplyUrl) { this.externalApplyUrl = externalApplyUrl; }
        public LocalDate getDeadline() { return deadline; }
        public void setDeadline(LocalDate deadline) { this.deadline = deadline; }
        public List<String> getMandatorySkills() { return mandatorySkills; }
        public void setMandatorySkills(List<String> mandatorySkills) { this.mandatorySkills = mandatorySkills; }
        public List<String> getOptionalSkills() { return optionalSkills; }
        public void setOptionalSkills(List<String> optionalSkills) { this.optionalSkills = optionalSkills; }
    }

    public static class OpportunityResponse {
        private Long id;
        private Long companyId;
        private String companyName;
        private String companyLogoUrl;
        private String title;
        private String description;
        private JobType jobType;
        private WorkMode workMode;
        private String location;
        private BigDecimal minCgpa;
        private String allowedDegrees;
        private Integer targetGraduationYear;
        private String allowedDepartments;
        private String salaryOrStipend;
        private String experienceRequirements;
        private String recruiterContact;
        private String externalApplyUrl;
        private LocalDate deadline;
        private OpportunityStatus status;
        private List<String> requiredSkills;
        private EligibilityResultDTO eligibility;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getCompanyId() { return companyId; }
        public void setCompanyId(Long companyId) { this.companyId = companyId; }
        public String getCompanyName() { return companyName; }
        public void setCompanyName(String companyName) { this.companyName = companyName; }
        public String getCompanyLogoUrl() { return companyLogoUrl; }
        public void setCompanyLogoUrl(String companyLogoUrl) { this.companyLogoUrl = companyLogoUrl; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public JobType getJobType() { return jobType; }
        public void setJobType(JobType jobType) { this.jobType = jobType; }
        public WorkMode getWorkMode() { return workMode; }
        public void setWorkMode(WorkMode workMode) { this.workMode = workMode; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public BigDecimal getMinCgpa() { return minCgpa; }
        public void setMinCgpa(BigDecimal minCgpa) { this.minCgpa = minCgpa; }
        public String getAllowedDegrees() { return allowedDegrees; }
        public void setAllowedDegrees(String allowedDegrees) { this.allowedDegrees = allowedDegrees; }
        public Integer getTargetGraduationYear() { return targetGraduationYear; }
        public void setTargetGraduationYear(Integer targetGraduationYear) { this.targetGraduationYear = targetGraduationYear; }
        public String getAllowedDepartments() { return allowedDepartments; }
        public void setAllowedDepartments(String allowedDepartments) { this.allowedDepartments = allowedDepartments; }
        public String getSalaryOrStipend() { return salaryOrStipend; }
        public void setSalaryOrStipend(String salaryOrStipend) { this.salaryOrStipend = salaryOrStipend; }
        public String getExperienceRequirements() { return experienceRequirements; }
        public void setExperienceRequirements(String experienceRequirements) { this.experienceRequirements = experienceRequirements; }
        public String getRecruiterContact() { return recruiterContact; }
        public void setRecruiterContact(String recruiterContact) { this.recruiterContact = recruiterContact; }
        public String getExternalApplyUrl() { return externalApplyUrl; }
        public void setExternalApplyUrl(String externalApplyUrl) { this.externalApplyUrl = externalApplyUrl; }
        public LocalDate getDeadline() { return deadline; }
        public void setDeadline(LocalDate deadline) { this.deadline = deadline; }
        public OpportunityStatus getStatus() { return status; }
        public void setStatus(OpportunityStatus status) { this.status = status; }
        public List<String> getRequiredSkills() { return requiredSkills; }
        public void setRequiredSkills(List<String> requiredSkills) { this.requiredSkills = requiredSkills; }
        public EligibilityResultDTO getEligibility() { return eligibility; }
        public void setEligibility(EligibilityResultDTO eligibility) { this.eligibility = eligibility; }
    }

    public static class EligibilityResultDTO {
        private boolean eligible;
        private boolean confirmable;
        private int skillMatchPercentage;
        private List<String> matchedSkills;
        private List<String> missingSkills;
        private List<String> unmetCriteriaReasons;

        public EligibilityResultDTO() {}

        public EligibilityResultDTO(boolean eligible, boolean confirmable, int skillMatchPercentage,
                                    List<String> matchedSkills, List<String> missingSkills,
                                    List<String> unmetCriteriaReasons) {
            this.eligible = eligible;
            this.confirmable = confirmable;
            this.skillMatchPercentage = skillMatchPercentage;
            this.matchedSkills = matchedSkills;
            this.missingSkills = missingSkills;
            this.unmetCriteriaReasons = unmetCriteriaReasons;
        }

        public boolean isEligible() { return eligible; }
        public void setEligible(boolean eligible) { this.eligible = eligible; }
        public boolean isConfirmable() { return confirmable; }
        public void setConfirmable(boolean confirmable) { this.confirmable = confirmable; }
        public int getSkillMatchPercentage() { return skillMatchPercentage; }
        public void setSkillMatchPercentage(int skillMatchPercentage) { this.skillMatchPercentage = skillMatchPercentage; }
        public List<String> getMatchedSkills() { return matchedSkills; }
        public void setMatchedSkills(List<String> matchedSkills) { this.matchedSkills = matchedSkills; }
        public List<String> getMissingSkills() { return missingSkills; }
        public void setMissingSkills(List<String> missingSkills) { this.missingSkills = missingSkills; }
        public List<String> getUnmetCriteriaReasons() { return unmetCriteriaReasons; }
        public void setUnmetCriteriaReasons(List<String> unmetCriteriaReasons) { this.unmetCriteriaReasons = unmetCriteriaReasons; }
    }
}
