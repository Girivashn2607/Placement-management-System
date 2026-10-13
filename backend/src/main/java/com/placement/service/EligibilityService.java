package com.placement.service;

import com.placement.dto.OpportunityDTO;
import com.placement.entity.Opportunity;
import com.placement.entity.OpportunitySkill;
import com.placement.entity.StudentProfile;
import com.placement.entity.StudentSkill;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class EligibilityService {

    public OpportunityDTO.EligibilityResultDTO evaluateEligibility(StudentProfile profile, Opportunity opportunity) {
        OpportunityDTO.EligibilityResultDTO result = new OpportunityDTO.EligibilityResultDTO();
        List<String> unmetCriteria = new ArrayList<>();
        List<String> matchedSkills = new ArrayList<>();
        List<String> missingSkills = new ArrayList<>();

        if (profile == null) {
            result.setConfirmable(false);
            result.setEligible(false);
            result.setSkillMatchPercentage(0);
            result.setUnmetCriteriaReasons(List.of("Student profile not found or incomplete"));
            result.setMatchedSkills(matchedSkills);
            result.setMissingSkills(missingSkills);
            return result;
        }

        // 1. CGPA check
        if (opportunity.getMinCgpa() != null && opportunity.getMinCgpa().doubleValue() > 0) {
            if (profile.getCgpa() == null) {
                unmetCriteria.add("CGPA information is missing in student profile.");
            } else if (profile.getCgpa().compareTo(opportunity.getMinCgpa()) < 0) {
                unmetCriteria.add(String.format("Minimum CGPA required is %.2f (Your CGPA: %.2f)",
                        opportunity.getMinCgpa(), profile.getCgpa()));
            }
        }

        // 2. Graduation Year check
        if (opportunity.getTargetGraduationYear() != null && opportunity.getTargetGraduationYear() > 0) {
            if (profile.getGraduationYear() == null) {
                unmetCriteria.add("Graduation year missing in student profile.");
            } else if (!profile.getGraduationYear().equals(opportunity.getTargetGraduationYear())) {
                unmetCriteria.add(String.format("Batch restriction: Eligible graduation year is %d (Your batch: %d)",
                        opportunity.getTargetGraduationYear(), profile.getGraduationYear()));
            }
        }

        // 3. Degree check
        if (opportunity.getAllowedDegrees() != null && !opportunity.getAllowedDegrees().isBlank()) {
            String studentDegree = profile.getDegree() != null ? profile.getDegree().trim().toLowerCase() : "";
            boolean degreeMatch = false;
            for (String allowed : opportunity.getAllowedDegrees().split(",")) {
                if (studentDegree.contains(allowed.trim().toLowerCase())) {
                    degreeMatch = true;
                    break;
                }
            }
            if (!degreeMatch) {
                unmetCriteria.add("Allowed degrees: " + opportunity.getAllowedDegrees() + " (Your degree: " + profile.getDegree() + ")");
            }
        }

        // 4. Department check
        if (opportunity.getAllowedDepartments() != null && !opportunity.getAllowedDepartments().isBlank()) {
            String studentDept = profile.getDepartment() != null ? profile.getDepartment().trim().toLowerCase() : "";
            boolean deptMatch = false;
            for (String allowed : opportunity.getAllowedDepartments().split(",")) {
                if (studentDept.contains(allowed.trim().toLowerCase())) {
                    deptMatch = true;
                    break;
                }
            }
            if (!deptMatch) {
                unmetCriteria.add("Eligible departments: " + opportunity.getAllowedDepartments() + " (Your department: " + profile.getDepartment() + ")");
            }
        }

        // 5. Skills analysis
        Set<String> studentSkillNames = new HashSet<>();
        if (profile.getSkills() != null) {
            for (StudentSkill s : profile.getSkills()) {
                if (s.getSkill() != null && s.getSkill().getName() != null) {
                    studentSkillNames.add(s.getSkill().getName().trim().toLowerCase());
                }
            }
        }

        List<OpportunitySkill> required = opportunity.getRequiredSkills();
        int totalSkills = required != null ? required.size() : 0;
        int matchedCount = 0;

        if (totalSkills > 0) {
            for (OpportunitySkill os : required) {
                String reqName = os.getSkill().getName();
                if (studentSkillNames.contains(reqName.trim().toLowerCase())) {
                    matchedSkills.add(reqName);
                    matchedCount++;
                } else {
                    missingSkills.add(reqName);
                    if (os.isMandatory()) {
                        unmetCriteria.add("Missing mandatory skill: " + reqName);
                    }
                }
            }
        }

        int matchPercentage = totalSkills > 0 ? (int) Math.round(((double) matchedCount / totalSkills) * 100) : 100;

        result.setConfirmable(true);
        result.setEligible(unmetCriteria.isEmpty());
        result.setSkillMatchPercentage(matchPercentage);
        result.setMatchedSkills(matchedSkills);
        result.setMissingSkills(missingSkills);
        result.setUnmetCriteriaReasons(unmetCriteria);

        return result;
    }
}
