package com.assetguard.app.repository;

import com.assetguard.app.model.Asset;
import com.assetguard.app.model.Asset.AssetStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface AssetRepository extends JpaRepository<Asset, Long> {

    // User-specific queries
    List<Asset> findByUserId(Long userId);

    Page<Asset> findByUserId(Long userId, Pageable pageable);

    Optional<Asset> findByIdAndUserId(Long id, Long userId);

    // Filter queries
    List<Asset> findByUserIdAndType(Long userId, String type);

    List<Asset> findByUserIdAndStatus(Long userId, AssetStatus status);

    // Search query
    @Query("SELECT a FROM Asset a WHERE a.user.id = :userId AND " +
            "(LOWER(a.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(a.type) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(a.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<Asset> searchByUser(@Param("userId") Long userId, @Param("search") String search);

    // Statistics queries
    @Query("SELECT COALESCE(SUM(a.value), 0) FROM Asset a WHERE a.user.id = :userId")
    Double getTotalValueByUser(@Param("userId") Long userId);

    @Query("SELECT COUNT(a) FROM Asset a WHERE a.user.id = :userId")
    Long getCountByUser(@Param("userId") Long userId);

    @Query("SELECT a.type, COUNT(a), COALESCE(SUM(a.value), 0) FROM Asset a WHERE a.user.id = :userId GROUP BY a.type")
    List<Object[]> getStatsByTypeForUser(@Param("userId") Long userId);

    @Query("SELECT a.status, COUNT(a) FROM Asset a WHERE a.user.id = :userId GROUP BY a.status")
    List<Object[]> getStatusCountsForUser(@Param("userId") Long userId);
}
