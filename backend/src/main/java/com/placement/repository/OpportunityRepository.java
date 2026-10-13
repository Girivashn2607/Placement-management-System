package com.placement.repository;

import com.placement.entity.Opportunity;
import com.placement.entity.OpportunityStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OpportunityRepository extends JpaRepository<Opportunity, Long> {
    Page<Opportunity> findByStatus(OpportunityStatus status, Pageable pageable);
    List<Opportunity> findByStatus(OpportunityStatus status);
    List<Opportunity> findByCompanyId(Long companyId);
    Page<Opportunity> findByCompanyId(Long companyId, Pageable pageable);

    @Query("SELECT o FROM Opportunity o WHERE o.status = :status AND " +
           "(:keyword IS NULL OR LOWER(o.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(o.company.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:jobType IS NULL OR CAST(o.jobType AS string) = :jobType) AND " +
           "(:workMode IS NULL OR CAST(o.workMode AS string) = :workMode)")
    Page<Opportunity> searchPublishedOpportunities(
            @Param("status") OpportunityStatus status,
            @Param("keyword") String keyword,
            @Param("jobType") String jobType,
            @Param("workMode") String workMode,
            Pageable pageable);

    long countByStatus(OpportunityStatus status);
}
