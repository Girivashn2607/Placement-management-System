package com.placement.service;

import com.placement.dto.StudentDTO;
import com.placement.entity.*;
import com.placement.repository.SkillRepository;
import com.placement.repository.StudentProfileRepository;
import com.placement.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentService {

    private final StudentProfileRepository studentProfileRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;

    public StudentService(StudentProfileRepository studentProfileRepository,
                          UserRepository userRepository,
                          SkillRepository skillRepository) {
        this.studentProfileRepository = studentProfileRepository;
        this.userRepository = userRepository;
        this.skillRepository = skillRepository;
    }

    @Transactional(readOnly = true)
    public StudentDTO.ProfileResponse getProfileByUserId(Long userId) {
        StudentProfile profile = studentProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Student profile not found for user ID: " + userId));

        return toDto(profile);
    }

    @Transactional
    public StudentDTO.ProfileResponse updateProfile(Long userId, StudentDTO.ProfileUpdateRequest request) {
        StudentProfile profile = studentProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Student profile not found for user ID: " + userId));

        User user = profile.getUser();
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
            userRepository.save(user);
        }

        profile.setRegistrationNumber(request.getRegistrationNumber());
        profile.setCollege(request.getCollege());
        profile.setDepartment(request.getDepartment());
        profile.setDegree(request.getDegree());
        profile.setSpecialization(request.getSpecialization());
        profile.setGraduationYear(request.getGraduationYear());
        profile.setCgpa(request.getCgpa());
        profile.setPreferredRoles(request.getPreferredRoles());
        profile.setPreferredLocations(request.getPreferredLocations());

        // Update Skills
        if (request.getSkills() != null) {
            profile.getSkills().clear();
            for (String skillName : request.getSkills()) {
                if (skillName != null && !skillName.isBlank()) {
                    Skill skill = skillRepository.findByNameIgnoreCase(skillName.trim())
                            .orElseGet(() -> skillRepository.save(new Skill(skillName.trim(), "General")));
                    profile.getSkills().add(new StudentSkill(profile, skill, "INTERMEDIATE"));
                }
            }
        }

        // Update Projects
        if (request.getProjects() != null) {
            profile.getProjects().clear();
            for (StudentDTO.ProjectItem item : request.getProjects()) {
                StudentProject project = new StudentProject();
                project.setStudentProfile(profile);
                project.setTitle(item.getTitle());
                project.setDescription(item.getDescription());
                project.setTechnologies(item.getTechnologies());
                project.setProjectUrl(item.getProjectUrl());
                project.setStartDate(item.getStartDate());
                project.setEndDate(item.getEndDate());
                profile.getProjects().add(project);
            }
        }

        // Update Experiences
        if (request.getExperiences() != null) {
            profile.getExperiences().clear();
            for (StudentDTO.ExperienceItem item : request.getExperiences()) {
                StudentExperience exp = new StudentExperience();
                exp.setStudentProfile(profile);
                exp.setTitle(item.getTitle());
                exp.setOrganization(item.getOrganization());
                exp.setExperienceType(item.getExperienceType() != null ? item.getExperienceType() : "INTERNSHIP");
                exp.setDescription(item.getDescription());
                exp.setStartDate(item.getStartDate());
                exp.setEndDate(item.getEndDate());
                profile.getExperiences().add(exp);
            }
        }

        // Recalculate completion percentage
        profile.setProfileCompletionPercentage(calculateCompletionPercentage(profile));

        StudentProfile updated = studentProfileRepository.save(profile);
        return toDto(updated);
    }

    public int calculateCompletionPercentage(StudentProfile p) {
        int score = 0;
        if (p.getRegistrationNumber() != null && !p.getRegistrationNumber().isBlank()) score += 15;
        if (p.getCollege() != null && !p.getCollege().isBlank()) score += 10;
        if (p.getDepartment() != null && !p.getDepartment().isBlank()) score += 10;
        if (p.getCgpa() != null) score += 15;
        if (p.getGraduationYear() != null) score += 10;
        if (p.getSkills() != null && !p.getSkills().isEmpty()) score += 20;
        if (p.getProjects() != null && !p.getProjects().isEmpty()) score += 10;
        if (p.getExperiences() != null && !p.getExperiences().isEmpty()) score += 10;
        return Math.min(score, 100);
    }

    public StudentDTO.ProfileResponse toDto(StudentProfile p) {
        StudentDTO.ProfileResponse dto = new StudentDTO.ProfileResponse();
        dto.setId(p.getId());
        dto.setUserId(p.getUser().getId());
        dto.setFullName(p.getUser().getFullName());
        dto.setEmail(p.getUser().getEmail());
        dto.setPhoneNumber(p.getUser().getPhoneNumber());
        dto.setRegistrationNumber(p.getRegistrationNumber());
        dto.setCollege(p.getCollege());
        dto.setDepartment(p.getDepartment());
        dto.setDegree(p.getDegree());
        dto.setSpecialization(p.getSpecialization());
        dto.setGraduationYear(p.getGraduationYear());
        dto.setCgpa(p.getCgpa());
        dto.setPreferredRoles(p.getPreferredRoles());
        dto.setPreferredLocations(p.getPreferredLocations());
        dto.setProfileCompletionPercentage(p.getProfileCompletionPercentage());

        if (p.getSkills() != null) {
            dto.setSkills(p.getSkills().stream()
                    .map(s -> new StudentDTO.SkillItem(s.getId(), s.getSkill().getName(), s.getProficiencyLevel()))
                    .collect(Collectors.toList()));
        } else {
            dto.setSkills(new ArrayList<>());
        }

        if (p.getProjects() != null) {
            dto.setProjects(p.getProjects().stream().map(pr -> {
                StudentDTO.ProjectItem item = new StudentDTO.ProjectItem();
                item.setId(pr.getId());
                item.setTitle(pr.getTitle());
                item.setDescription(pr.getDescription());
                item.setTechnologies(pr.getTechnologies());
                item.setProjectUrl(pr.getProjectUrl());
                item.setStartDate(pr.getStartDate());
                item.setEndDate(pr.getEndDate());
                return item;
            }).collect(Collectors.toList()));
        } else {
            dto.setProjects(new ArrayList<>());
        }

        if (p.getExperiences() != null) {
            dto.setExperiences(p.getExperiences().stream().map(e -> {
                StudentDTO.ExperienceItem item = new StudentDTO.ExperienceItem();
                item.setId(e.getId());
                item.setTitle(e.getTitle());
                item.setOrganization(e.getOrganization());
                item.setExperienceType(e.getExperienceType());
                item.setDescription(e.getDescription());
                item.setStartDate(e.getStartDate());
                item.setEndDate(e.getEndDate());
                return item;
            }).collect(Collectors.toList()));
        } else {
            dto.setExperiences(new ArrayList<>());
        }

        return dto;
    }
}
