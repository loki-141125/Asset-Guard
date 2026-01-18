package com.assetguard.app.service;

import com.assetguard.app.model.Asset;
import com.assetguard.app.model.MaintenanceRecord;
import com.assetguard.app.repository.AssetRepository;
import com.assetguard.app.repository.MaintenanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MaintenanceService {

    @Autowired
    private MaintenanceRepository maintenanceRepository;

    @Autowired
    private AssetRepository assetRepository;

    public List<MaintenanceRecord> getUserMaintenanceRecords(Long userId) {
        return maintenanceRepository.findAllByUserId(userId);
    }

    public List<MaintenanceRecord> getAssetMaintenanceRecords(Long assetId, Long userId) {
        // Verify asset belongs to user
        assetRepository.findByIdAndUserId(assetId, userId)
                .orElseThrow(() -> new RuntimeException("Asset not found or access denied"));
        return maintenanceRepository.findByAssetId(assetId);
    }

    public MaintenanceRecord addMaintenanceRecord(MaintenanceRecord record, Long assetId, Long userId) {
        Asset asset = assetRepository.findByIdAndUserId(assetId, userId)
                .orElseThrow(() -> new RuntimeException("Asset not found or access denied"));

        record.setAsset(asset);
        return maintenanceRepository.save(record);
    }

    public Double getTotalMaintenanceCost(Long userId) {
        Double total = maintenanceRepository.getTotalMaintenanceCost(userId);
        return total != null ? total : 0.0;
    }
}
