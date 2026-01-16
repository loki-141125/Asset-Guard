package com.assetguard.app.service;

import com.assetguard.app.model.Asset;
import com.assetguard.app.model.User;
import com.assetguard.app.repository.AssetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AssetService {

    @Autowired
    private AssetRepository assetRepository;

    public List<Asset> getAssetsByUser(User user) {
        return assetRepository.findByUser(user);
    }

    public void saveAsset(Asset asset) {
        assetRepository.save(asset);
    }

    public Double getTotalValue(User user) {
        return assetRepository.findByUser(user).stream()
                .mapToDouble(val -> val.getValue() != null ? val.getValue() : 0.0)
                .sum();
    }
}
