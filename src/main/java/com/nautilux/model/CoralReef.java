package com.nautilux.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "coral_reefs")
public class CoralReef extends PanacheEntity {
    
    @NotBlank
    @Column(name = "name", nullable = false)
    public String name;
    
    @Column(name = "description", columnDefinition = "TEXT")
    public String description;
    
    @NotNull
    @Column(name = "latitude", nullable = false)
    public Double latitude;
    
    @NotNull
    @Column(name = "longitude", nullable = false)
    public Double longitude;
    
    @Column(name = "depth_meters")
    public Double depthMeters;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "reef_type")
    public ReefType reefType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "health_status")
    public HealthStatus healthStatus = HealthStatus.UNKNOWN;
    
    @Column(name = "health_score")
    public Double healthScore;
    
    @Column(name = "bleaching_risk_level")
    public Integer bleachingRiskLevel;
    
    @Column(name = "biodiversity_index")
    public Double biodiversityIndex;
    
    @Column(name = "water_temperature_celsius")
    public Double waterTemperatureCelsius;
    
    @Column(name = "salinity_ppt")
    public Double salinityPpt;
    
    @Column(name = "ph_level")
    public Double phLevel;
    
    @Column(name = "turbidity_ntu")
    public Double turbidityNtu;
    
    @Column(name = "created_at", nullable = false)
    public LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    public LocalDateTime updatedAt = LocalDateTime.now();
    
    @OneToMany(mappedBy = "coralReef", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<ReefZone> reefZones;
    
    @OneToMany(mappedBy = "coralReef", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<SensorData> sensorData;
    
    @OneToMany(mappedBy = "coralReef", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<ImageData> imageData;
    
    @OneToMany(mappedBy = "coralReef", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<SonarData> sonarData;
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    public enum ReefType {
        FRINGING, BARRIER, ATOLL, PATCH, PLATFORM
    }
    
    public enum HealthStatus {
        EXCELLENT, GOOD, FAIR, POOR, CRITICAL, UNKNOWN
    }
} 