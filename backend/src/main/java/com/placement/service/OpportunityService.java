package com.placement.service;

import com.placement.dto.OpportunityDTO;
import com.placement.entity.*;
import com.placement.repository.CompanyRepository;
import com.placement.repository.OpportunityRepository;
import com.placement.repository.SkillRepository;
import com.placement.repository.StudentProfileRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OpportunityService {

    private final OpportunityRepository opportunityRepository;
    private final CompanyRepository companyRepository;
    private final SkillRepository skillRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final EligibilityService eligibilityService;

    public OpportunityService(OpportunityRepository opportunityRepository,
                              CompanyRepository companyRepository,
                              SkillRepository skillRepository,
                              StudentProfileRepository studentProfileRepository,
                              EligibilityService eligibilityService) {
        this.opportunityRepository = opportunityRepository;
        this.companyRepository = companyRepository;
        this.skillRepository = skillRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.eligibilityService = eligibilityService;
    }

    @Transactional(readOnly = true)
    public Page<OpportunityDTO.OpportunityResponse> getPublishedOpportunities(
            String keyword, String jobType, String workMode, Long studentUserId, Pageable pageable) {

        StudentProfile studentProfile = null;
        if (studentUserId != null) {
            studentProfile = studentProfileRepository.findByUserId(studentUserId).orElse(null);
        }

        final StudentProfile finalProfile = studentProfile;
        Page<Opportunity> page = opportunityRepository.searchPublishedOpportunities(
                OpportunityStatus.PUBLISHED, keyword, jobType, workMode, pageable);

        return page.map(opp -> toResponseDto(opp, finalProfile));
    }

    @Transactional(readOnly = true)
    public OpportunityDTO.OpportunityResponse getOpportunityById(Long id, Long studentUserId) {
        Opportunity opp = opportunityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Opportunity not found with ID: " + id));

        StudentProfile profile = null;
        if (studentUserId != null) {
            profile = studentProfileRepository.findByUserId(studentUserId).orElse(null);
        }

        return toResponseDto(opp, profile);
    }

    @Transactional
    public Opportunity createOpportunity(OpportunityDTO.OpportunityRequest request, User creator, boolean autoPublish) {
        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new IllegalArgumentException("Company not found with ID: " + request.getCompanyId()));

        Opportunity opp = new Opportunity();
        opp.setCompany(company);
        opp.setTitle(request.getTitle());
        opp.setDescription(request.getDescription());
        opp.setJobType(request.getJobType());
        opp.setWorkMode(request.getWorkMode());
        opp.setLocation(request.getLocation());
        opp.setMinCgpa(request.getMinCgpa());
        opp.setAllowedDegrees(request.getAllowedDegrees());
        opp.setTargetGraduationYear(request.getTargetGraduationYear());
        opp.setAllowedDepartments(request.getAllowedDepartments());
        opp.setSalaryOrStipend(request.getSalaryOrStipend());
        opp.setExperienceRequirements(request.getExperienceRequirements());
        opp.setRecruiterContact(request.getRecruiterContact());
        opp.setExternalApplyUrl(request.getExternalApplyUrl());
        opp.setDeadline(request.getDeadline());
        opp.setCreatedBy(creator);
        opp.setStatus(autoPublish ? OpportunityStatus.PUBLISHED : OpportunityStatus.PENDING_APPROVAL);

        List<OpportunitySkill> skills = new ArrayList<>();
        if (request.getMandatorySkills() != null) {
            for (String sName : request.getMandatorySkills()) {
                if (sName != null && !sName.isBlank()) {
                    Skill sk = skillRepository.findByNameIgnoreCase(sName.trim())
                            .orElseGet(() -> skillRepository.save(new Skill(sName.trim(), "General")));
                    skills.add(new OpportunitySkill(opp, sk, true));
                }
            }
        }
        if (request.getOptionalSkills() != null) {
            for (String sName : request.getOptionalSkills()) {
                if (sName != null && !sName.isBlank()) {
                    Skill sk = skillRepository.findByNameIgnoreCase(sName.trim())
                            .orElseGet(() -> skillRepository.save(new Skill(sName.trim(), "General")));
                    skills.add(new OpportunitySkill(opp, sk, false));
                }
            }
        }
        opp.setRequiredSkills(skills);

        return opportunityRepository.save(opp);
    }

    @Transactional
    public void updateStatus(Long opportunityId, OpportunityStatus status) {
        Opportunity opp = opportunityRepository.findById(opportunityId)
                .orElseThrow(() -> new IllegalArgumentException("Opportunity not found with ID: " + opportunityId));
        opp.setStatus(status);
        opportunityRepository.save(opp);
    }

    public OpportunityDTO.OpportunityResponse toResponseDto(Opportunity opp, StudentProfile studentProfile) {
        OpportunityDTO.OpportunityResponse dto = new OpportunityDTO.OpportunityResponse();
        dto.setId(opp.getId());
        dto.setCompanyId(opp.getCompany().getId());
        dto.setCompanyName(opp.getCompany().getName());
        dto.setCompanyLogoUrl(opp.getCompany().getLogoUrl());
        dto.setTitle(opp.getTitle());
        dto.setDescription(opp.getDescription());
        dto.setJobType(opp.getJobType());
        dto.setWorkMode(opp.getWorkMode());
        dto.setLocation(opp.getLocation());
        dto.setMinCgpa(opp.getMinCgpa());
        dto.setAllowedDegrees(opp.getAllowedDegrees());
        dto.setTargetGraduationYear(opp.getTargetGraduationYear());
        dto.setAllowedDepartments(opp.getAllowedDepartments());
        dto.setSalaryOrStipend(opp.getSalaryOrStipend());
        dto.setExperienceRequirements(opp.getExperienceRequirements());
        dto.setRecruiterContact(opp.getRecruiterContact());
        dto.setExternalApplyUrl(opp.getExternalApplyUrl());
        dto.setDeadline(opp.getDeadline());
        dto.setStatus(opp.getStatus());

        if (opp.getRequiredSkills() != null) {
            dto.setRequiredSkills(opp.getRequiredSkills().stream()
                    .map(os -> os.getSkill().getName() + (os.isMandatory() ? " (Mandatory)" : ""))
                    .collect(Collectors.toList()));
        } else {
            dto.setRequiredSkills(new ArrayList<>());
        }

        if (studentProfile != null) {
            dto.setEligibility(eligibilityService.evaluateEligibility(studentProfile, opp));
        }

        return dto;
    }
}
