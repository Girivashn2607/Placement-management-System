package com.placement.repository;

import com.placement.entity.ResumeAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResumeAnalysisRepository extends JpaRepository<ResumeAnalysis, Long> {
    Optional<ResumeAnalysis> findFirstByResumeIdOrderByCreatedAtDesc(Long resumeId);
    Optional<ResumeAnalysis> findFirstByStudentProfileIdOrderByCreatedAtDesc(Long studentProfileId);
}
