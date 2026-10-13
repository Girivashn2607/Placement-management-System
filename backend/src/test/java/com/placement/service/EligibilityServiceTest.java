package com.placement.service;

import com.placement.dto.OpportunityDTO;
import com.placement.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EligibilityServiceTest {

    private EligibilityService eligibilityService;
    private StudentProfile profile;
    private Opportunity opportunity;

    @BeforeEach
    public void setup() {
        eligibilityService = new EligibilityService();

        User user = new User("student@test.com", "hash", "Test Student", "1234567890", Role.STUDENT);
        profile = new StudentProfile();
        profile.setUser(user);
        profile.setDegree("B.Tech");
        profile.setDepartment("Computer Science and Engineering");
        profile.setGraduationYear(2026);
        profile.setCgpa(BigDecimal.valueOf(8.50));

        Skill s1 = new Skill("Java", "Backend");
        Skill s2 = new Skill("Spring Boot", "Backend");
        List<StudentSkill> studentSkills = new ArrayList<>();
        studentSkills.add(new StudentSkill(profile, s1, "INTERMEDIATE"));
        studentSkills.add(new StudentSkill(profile, s2, "ADVANCED"));
        profile.setSkills(studentSkills);

        opportunity = new Opportunity();
        opportunity.setTitle("Software Engineer 1");
        opportunity.setMinCgpa(BigDecimal.valueOf(7.50));
        opportunity.setAllowedDegrees("B.Tech, M.Tech");
        opportunity.setAllowedDepartments("Computer Science and Engineering, Information Technology");
        opportunity.setTargetGraduationYear(2026);

        OpportunitySkill os1 = new OpportunitySkill(opportunity, s1, true);
        OpportunitySkill os2 = new OpportunitySkill(opportunity, s2, true);
        opportunity.setRequiredSkills(List.of(os1, os2));
    }

    @Test
    public void testEligibleStudent() {
        OpportunityDTO.EligibilityResultDTO result = eligibilityService.evaluateEligibility(profile, opportunity);
        assertTrue(result.isEligible(), "Student meeting all criteria must be eligible");
        assertEquals(100, result.getSkillMatchPercentage());
        assertTrue(result.getUnmetCriteriaReasons().isEmpty());
    }

    @Test
    public void testIneligibleDueToLowCgpa() {
        profile.setCgpa(BigDecimal.valueOf(6.80)); // Below 7.50
        OpportunityDTO.EligibilityResultDTO result = eligibilityService.evaluateEligibility(profile, opportunity);
        assertFalse(result.isEligible(), "Student below cutoff CGPA must not be eligible");
        assertTrue(result.getUnmetCriteriaReasons().stream().anyMatch(r -> r.contains("Minimum CGPA")));
    }

    @Test
    public void testIneligibleDueToMissingMandatorySkill() {
        Skill missing = new Skill("Docker", "DevOps");
        OpportunitySkill os3 = new OpportunitySkill(opportunity, missing, true);
        opportunity.setRequiredSkills(List.of(os3));

        OpportunityDTO.EligibilityResultDTO result = eligibilityService.evaluateEligibility(profile, opportunity);
        assertFalse(result.isEligible(), "Missing mandatory skill must make student ineligible");
        assertTrue(result.getMissingSkills().contains("Docker"));
    }
}
