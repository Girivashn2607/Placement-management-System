package com.placement.service;

import com.placement.entity.*;
import com.placement.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminReportService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final OpportunityRepository opportunityRepository;
    private final PlacementDriveRepository placementDriveRepository;
    private final ComplaintRepository complaintRepository;
    private final StudentProfileRepository studentProfileRepository;

    public AdminReportService(UserRepository userRepository,
                              CompanyRepository companyRepository,
                              OpportunityRepository opportunityRepository,
                              PlacementDriveRepository placementDriveRepository,
                              ComplaintRepository complaintRepository,
                              StudentProfileRepository studentProfileRepository) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.opportunityRepository = opportunityRepository;
        this.placementDriveRepository = placementDriveRepository;
        this.complaintRepository = complaintRepository;
        this.studentProfileRepository = studentProfileRepository;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getDashboardOverview() {
        Map<String, Object> stats = new HashMap<>();

        long totalStudents = userRepository.countByRole(Role.STUDENT);
        long totalCompanies = companyRepository.count();
        long activeOpportunities = opportunityRepository.countByStatus(OpportunityStatus.PUBLISHED);
        long totalDrives = placementDriveRepository.count();

        Map<String, Long> complaintsByStatus = new HashMap<>();
        for (ComplaintStatus s : ComplaintStatus.values()) {
            complaintsByStatus.put(s.name(), complaintRepository.countByStatus(s));
        }

        // Department-wise distribution
        List<StudentProfile> allStudents = studentProfileRepository.findAll();
        Map<String, Long> deptDistribution = allStudents.stream()
                .filter(s -> s.getDepartment() != null)
                .collect(Collectors.groupingBy(StudentProfile::getDepartment, Collectors.counting()));

        stats.put("totalStudents", totalStudents);
        stats.put("totalCompanies", totalCompanies);
        stats.put("activeOpportunities", activeOpportunities);
        stats.put("totalDrives", totalDrives);
        stats.put("complaintsByStatus", complaintsByStatus);
        stats.put("departmentDistribution", deptDistribution);

        return stats;
    }

    @Transactional(readOnly = true)
    public String exportStudentsCsv() {
        List<StudentProfile> students = studentProfileRepository.findAll();
        StringBuilder csv = new StringBuilder();
        csv.append("Registration Number,Full Name,Email,Phone,Department,Degree,Graduation Year,CGPA\n");

        for (StudentProfile s : students) {
            csv.append(String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",%d,%.2f\n",
                    s.getRegistrationNumber() != null ? s.getRegistrationNumber() : "",
                    s.getUser().getFullName(),
                    s.getUser().getEmail(),
                    s.getUser().getPhoneNumber() != null ? s.getUser().getPhoneNumber() : "",
                    s.getDepartment(),
                    s.getDegree(),
                    s.getGraduationYear(),
                    s.getCgpa() != null ? s.getCgpa().doubleValue() : 0.00
            ));
        }
        return csv.toString();
    }
}
