package com.placement.repository;

import com.placement.entity.StudentProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface StudentProfileRepository extends JpaRepository<StudentProfile, Long> {
    Optional<StudentProfile> findByUserId(Long userId);
    Optional<StudentProfile> findByRegistrationNumber(String registrationNumber);
    boolean existsByRegistrationNumber(String registrationNumber);

    @Query("SELECT s FROM StudentProfile s WHERE " +
           "(:dept IS NULL OR LOWER(s.department) = LOWER(:dept)) AND " +
           "(:gradYear IS NULL OR s.graduationYear = :gradYear) AND " +
           "(:minCgpa IS NULL OR s.cgpa >= :minCgpa)")
    Page<StudentProfile> findFilteredStudents(
            @Param("dept") String department,
            @Param("gradYear") Integer graduationYear,
            @Param("minCgpa") BigDecimal minCgpa,
            Pageable pageable);
}
