package com.placement.repository;

import com.placement.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> {
    List<Resume> findByStudentProfileIdOrderByCreatedAtDesc(Long studentProfileId);
    Optional<Resume> findFirstByStudentProfileIdOrderByCreatedAtDesc(Long studentProfileId);
}
