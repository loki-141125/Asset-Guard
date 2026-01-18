package com.assetguard.app.controller;

import com.assetguard.app.model.Asset;
import com.assetguard.app.model.Asset.AssetStatus;
import com.assetguard.app.model.User;
import com.assetguard.app.service.AssetService;
import com.assetguard.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/assets")
public class AssetApiController {

    @Autowired
    private AssetService assetService;

    @Autowired
    private UserService userService;

    private Long getCurrentUserId(Authentication auth) {
        User user = userService.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getId();
    }

    @GetMapping
    public ResponseEntity<?> getAllAssets(
            Authentication auth,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        Long userId = getCurrentUserId(auth);

        if (search != null && !search.isEmpty()) {
            List<Asset> assets = assetService.searchAssets(userId, search);
            return ResponseEntity.ok(assets);
        }

        if (type != null && !type.isEmpty()) {
            List<Asset> assets = assetService.getAssetsByType(userId, type);
            return ResponseEntity.ok(assets);
        }

        if (status != null && !status.isEmpty()) {
            List<Asset> assets = assetService.getAssetsByStatus(userId, AssetStatus.valueOf(status.toUpperCase()));
            return ResponseEntity.ok(assets);
        }

        Page<Asset> assets = assetService.getAssetsByUserPaged(userId, page, size, sortBy, direction);
        return ResponseEntity.ok(Map.of(
                "content", assets.getContent(),
                "totalPages", assets.getTotalPages(),
                "totalElements", assets.getTotalElements(),
                "currentPage", assets.getNumber()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAsset(@PathVariable Long id, Authentication auth) {
        Long userId = getCurrentUserId(auth);
        return assetService.getAssetByIdAndUser(id, userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createAsset(@RequestBody Asset asset, Authentication auth) {
        try {
            Long userId = getCurrentUserId(auth);
            Asset saved = assetService.createAsset(asset, userId);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAsset(@PathVariable Long id, @RequestBody Asset asset, Authentication auth) {
        try {
            Long userId = getCurrentUserId(auth);
            Asset updated = assetService.updateAsset(id, asset, userId);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAsset(@PathVariable Long id, Authentication auth) {
        try {
            Long userId = getCurrentUserId(auth);
            assetService.deleteAsset(id, userId);
            return ResponseEntity.ok(Map.of("message", "Asset deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getStats(Authentication auth) {
        Long userId = getCurrentUserId(auth);
        Map<String, Object> stats = assetService.getDashboardStats(userId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/types")
    public ResponseEntity<?> getAssetTypes() {
        return ResponseEntity.ok(List.of(
                "Electronics", "Vehicle", "Real Estate", "Investment",
                "Furniture", "Equipment", "Jewelry", "Collectible", "Other"));
    }

    @GetMapping("/statuses")
    public ResponseEntity<?> getAssetStatuses() {
        return ResponseEntity.ok(AssetStatus.values());
    }
}
