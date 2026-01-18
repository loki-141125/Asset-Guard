package com.assetguard.app.repository;

import com.assetguard.app.model.MaintenanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface MaintenanceRepository extends JpaRepository<MaintenanceRecord, Long> {
    List<MaintenanceRecord> findByAssetId(Long assetId);

    @Query("SELECT m FROM MaintenanceRecord m WHERE m.asset.user.id = :userId ORDER BY m.maintenanceDate DESC")
    List<MaintenanceRecord> findAllByUserId(@Param("userId") Long userId);

    @Query("SELECT SUM(m.cost) FROM MaintenanceRecord m WHERE m.asset.user.id = :userId")
    Double getTotalMaintenanceCost(@Param("userId") Long userId);
}
