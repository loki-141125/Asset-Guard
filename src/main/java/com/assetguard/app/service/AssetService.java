package com.assetguard.app.service;

import com.assetguard.app.model.Asset;
import com.assetguard.app.repository.AssetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AssetService {

    @Autowired
    private AssetRepository assetRepository;

    public List<Asset> getAllAssets() {
        return assetRepository.findAll();
    }

    public void saveAsset(Asset asset) {
        assetRepository.save(asset);
    }

    public Double getTotalValue() {
        return assetRepository.findAll().stream()
                .mapToDouble(val -> val.getValue() != null ? val.getValue() : 0.0)
                .sum();
    }
}
