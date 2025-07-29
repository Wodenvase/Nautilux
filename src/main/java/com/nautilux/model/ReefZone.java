package com.nautilux.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "reef_zones")
public class ReefZone extends PanacheEntity {
    
    @NotBlank
    @Column(name = "name", nullable = false)
    public String name;
    
    @Column(name = "description", columnDefinition = "TEXT")
    public String description;
    
    @NotNull
    @Column(name = "zone_type", nullable = false)
    @Enumerated(EnumType.STRING)
    public ZoneType zoneType;
    
    @Column(name = "depth_range_start")
    public Double depthRangeStart;
    
    @Column(name = "depth_range_end")
    public Double depthRangeEnd;
    
    @Column(name = "area_square_meters")
    public Double areaSquareMeters;
    
    @Column(name = "coral_coverage_percentage")
    public Double coralCoveragePercentage;
    
    @Column(name = "algae_coverage_percentage")
    public Double algaeCoveragePercentage;
    
    @Column(name = "sand_coverage_percentage")
    public Double sandCoveragePercentage;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "health_status")
    public CoralReef.HealthStatus healthStatus = CoralReef.HealthStatus.UNKNOWN;
    
    @Column(name = "health_score")
    public Double healthScore;
    
    @Column(name = "bleaching_risk_level")
    public Integer bleachingRiskLevel;
    
    @Column(name = "biodiversity_index")
    public Double biodiversityIndex;
    
    @Column(name = "created_at", nullable = false)
    public LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    public LocalDateTime updatedAt = LocalDateTime.now();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coral_reef_id", nullable = false)
    public CoralReef coralReef;
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    public enum ZoneType {
        FOREREEF, BACKREEF, LAGOON, REEF_FLAT, REEF_CREST, DEEP_SLOPE
    }
} 