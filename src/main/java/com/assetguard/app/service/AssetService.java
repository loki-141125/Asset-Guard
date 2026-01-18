package com.assetguard.app.service;

import com.assetguard.app.model.Asset;
import com.assetguard.app.model.Asset.AssetStatus;
import com.assetguard.app.model.User;
import com.assetguard.app.repository.AssetRepository;
import com.assetguard.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.*;

@Service
public class AssetService {

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private UserRepository userRepository;

    // ========== CRUD Operations ==========

    public List<Asset> getAllAssets() {
        return assetRepository.findAll();
    }

    public List<Asset> getAssetsByUser(Long userId) {
        return assetRepository.findByUserId(userId);
    }

    public Page<Asset> getAssetsByUserPaged(Long userId, int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return assetRepository.findByUserId(userId, pageable);
    }

    public Optional<Asset> getAssetById(Long id) {
        return assetRepository.findById(id);
    }

    public Optional<Asset> getAssetByIdAndUser(Long id, Long userId) {
        return assetRepository.findByIdAndUserId(id, userId);
    }

    public Asset saveAsset(Asset asset) {
        return assetRepository.save(asset);
    }

    public Asset createAsset(Asset asset, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        asset.setUser(user);
        if (asset.getPurchaseDate() == null) {
            asset.setPurchaseDate(LocalDate.now());
        }
        if (asset.getStatus() == null) {
            asset.setStatus(AssetStatus.ACTIVE);
        }
        return assetRepository.save(asset);
    }

    public Asset updateAsset(Long id, Asset updatedAsset, Long userId) {
        Asset asset = assetRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Asset not found or access denied"));

        if (updatedAsset.getName() != null)
            asset.setName(updatedAsset.getName());
        if (updatedAsset.getType() != null)
            asset.setType(updatedAsset.getType());
        if (updatedAsset.getValue() != null)
            asset.setValue(updatedAsset.getValue());
        if (updatedAsset.getPurchaseDate() != null)
            asset.setPurchaseDate(updatedAsset.getPurchaseDate());
        if (updatedAsset.getDescription() != null)
            asset.setDescription(updatedAsset.getDescription());
        if (updatedAsset.getImageUrl() != null)
            asset.setImageUrl(updatedAsset.getImageUrl());
        if (updatedAsset.getStatus() != null)
            asset.setStatus(updatedAsset.getStatus());
        if (updatedAsset.getLocation() != null)
            asset.setLocation(updatedAsset.getLocation());
        if (updatedAsset.getSerialNumber() != null)
            asset.setSerialNumber(updatedAsset.getSerialNumber());
        if (updatedAsset.getWarrantyExpiry() != null)
            asset.setWarrantyExpiry(updatedAsset.getWarrantyExpiry());

        return assetRepository.save(asset);
    }

    public void deleteAsset(Long id, Long userId) {
        Asset asset = assetRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Asset not found or access denied"));
        assetRepository.delete(asset);
    }

    // ========== Search & Filter ==========

    public List<Asset> searchAssets(Long userId, String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAssetsByUser(userId);
        }
        return assetRepository.searchByUser(userId, query.trim());
    }

    public List<Asset> getAssetsByType(Long userId, String type) {
        return assetRepository.findByUserIdAndType(userId, type);
    }

    public List<Asset> getAssetsByStatus(Long userId, AssetStatus status) {
        return assetRepository.findByUserIdAndStatus(userId, status);
    }

    // ========== Statistics ==========

    public Double getTotalValue() {
        return assetRepository.findAll().stream()
                .mapToDouble(val -> val.getValue() != null ? val.getValue() : 0.0)
                .sum();
    }

    public Double getTotalValueByUser(Long userId) {
        Double total = assetRepository.getTotalValueByUser(userId);
        return total != null ? total : 0.0;
    }

    public Long getAssetCountByUser(Long userId) {
        return assetRepository.getCountByUser(userId);
    }

    public Map<String, Object> getStatsByTypeForUser(Long userId) {
        List<Object[]> results = assetRepository.getStatsByTypeForUser(userId);
        Map<String, Object> stats = new HashMap<>();
        List<String> labels = new ArrayList<>();
        List<Long> counts = new ArrayList<>();
        List<Double> values = new ArrayList<>();

        for (Object[] row : results) {
            labels.add((String) row[0]);
            counts.add((Long) row[1]);
            values.add((Double) row[2]);
        }

        stats.put("labels", labels);
        stats.put("counts", counts);
        stats.put("values", values);
        return stats;
    }

    public Map<String, Long> getStatusCountsForUser(Long userId) {
        List<Object[]> results = assetRepository.getStatusCountsForUser(userId);
        Map<String, Long> statusCounts = new HashMap<>();

        for (Object[] row : results) {
            AssetStatus status = (AssetStatus) row[0];
            Long count = (Long) row[1];
            statusCounts.put(status.name(), count);
        }

        return statusCounts;
    }

    public Map<String, Object> getDashboardStats(Long userId) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalValue", getTotalValueByUser(userId));
        stats.put("assetCount", getAssetCountByUser(userId));
        stats.put("byType", getStatsByTypeForUser(userId));
        stats.put("byStatus", getStatusCountsForUser(userId));
        return stats;
    }
}
