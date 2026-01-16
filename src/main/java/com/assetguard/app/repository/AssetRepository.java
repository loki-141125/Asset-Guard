package com.assetguard.app.repository;

import com.assetguard.app.model.Asset;
import com.assetguard.app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AssetRepository extends JpaRepository<Asset, Long> {
    List<Asset> findByUser(User user);
}
