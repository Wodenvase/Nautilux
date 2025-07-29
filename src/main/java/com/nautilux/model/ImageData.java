package com.nautilux.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "image_data")
public class ImageData extends PanacheEntity {
    
    @NotNull
    @Column(name = "timestamp", nullable = false)
    public LocalDateTime timestamp;
    
    @Column(name = "image_url")
    public String imageUrl;
    
    @Column(name = "file_path")
    public String filePath;
    
    @Column(name = "file_name")
    public String fileName;
    
    @Column(name = "file_size_bytes")
    public Long fileSizeBytes;
    
    @Column(name = "image_format")
    public String imageFormat;
    
    @Column(name = "width_pixels")
    public Integer widthPixels;
    
    @Column(name = "height_pixels")
    public Integer heightPixels;
    
    @Column(name = "latitude")
    public Double latitude;
    
    @Column(name = "longitude")
    public Double longitude;
    
    @Column(name = "depth_meters")
    public Double depthMeters;
    
    @Column(name = "camera_model")
    public String cameraModel;
    
    @Column(name = "exposure_settings")
    public String exposureSettings;
    
    @Column(name = "water_conditions")
    public String waterConditions;
    
    @Column(name = "visibility_meters")
    public Double visibilityMeters;
    
    @Column(name = "ai_analysis_status")
    @Enumerated(EnumType.STRING)
    public AnalysisStatus aiAnalysisStatus = AnalysisStatus.PENDING;
    
    @Column(name = "coral_coverage_percentage")
    public Double coralCoveragePercentage;
    
    @Column(name = "bleaching_detected")
    public Boolean bleachingDetected;
    
    @Column(name = "bleaching_severity")
    public Integer bleachingSeverity;
    
    @Column(name = "species_detected")
    public String speciesDetected;
    
    @Column(name = "biodiversity_score")
    public Double biodiversityScore;
    
    @Column(name = "health_score")
    public Double healthScore;
    
    @Column(name = "confidence_score")
    public Double confidenceScore;
    
    @Column(name = "ai_model_version")
    public String aiModelVersion;
    
    @Column(name = "processing_time_ms")
    public Long processingTimeMs;
    
    @Column(name = "created_at", nullable = false)
    public LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    public LocalDateTime updatedAt = LocalDateTime.now();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coral_reef_id")
    public CoralReef coralReef;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reef_zone_id")
    public ReefZone reefZone;
    
    @OneToMany(mappedBy = "imageData", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<ImageAnnotation> annotations;
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    public enum AnalysisStatus {
        PENDING, PROCESSING, COMPLETED, FAILED, CANCELLED
    }
} 