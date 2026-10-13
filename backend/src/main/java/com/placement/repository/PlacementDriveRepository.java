package com.placement.repository;

import com.placement.entity.PlacementDrive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlacementDriveRepository extends JpaRepository<PlacementDrive, Long> {
    List<PlacementDrive> findAllByOrderByDriveDateAsc();
    List<PlacementDrive> findByCompanyId(Long companyId);
}
