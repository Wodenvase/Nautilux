package com.nautilux.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "sonar_data")
public class SonarData extends PanacheEntity {
    
    @NotNull
    @Column(name = "timestamp", nullable = false)
    public LocalDateTime timestamp;
    
    @Column(name = "file_path")
    public String filePath;
    
    @Column(name = "file_name")
    public String fileName;
    
    @Column(name = "file_size_bytes")
    public Long fileSizeBytes;
    
    @Column(name = "data_format")
    public String dataFormat;
    
    @Column(name = "latitude")
    public Double latitude;
    
    @Column(name = "longitude")
    public Double longitude;
    
    @Column(name = "depth_meters")
    public Double depthMeters;
    
    @Column(name = "sonar_frequency_hz")
    public Double sonarFrequencyHz;
    
    @Column(name = "pulse_length_ms")
    public Double pulseLengthMs;
    
    @Column(name = "transmit_power_watts")
    public Double transmitPowerWatts;
    
    @Column(name = "beam_width_degrees")
    public Double beamWidthDegrees;
    
    @Column(name = "range_meters")
    public Double rangeMeters;
    
    @Column(name = "sampling_rate_hz")
    public Double samplingRateHz;
    
    @Column(name = "data_points")
    public Integer dataPoints;
    
    @Column(name = "ai_analysis_status")
    @Enumerated(EnumType.STRING)
    public ImageData.AnalysisStatus aiAnalysisStatus = ImageData.AnalysisStatus.PENDING;
    
    @Column(name = "reef_structure_detected")
    public Boolean reefStructureDetected;
    
    @Column(name = "structure_height_meters")
    public Double structureHeightMeters;
    
    @Column(name = "structure_complexity_score")
    public Double structureComplexityScore;
    
    @Column(name = "fish_density_estimate")
    public Double fishDensityEstimate;
    
    @Column(name = "sediment_thickness_meters")
    public Double sedimentThicknessMeters;
    
    @Column(name = "water_column_height_meters")
    public Double waterColumnHeightMeters;
    
    @Column(name = "signal_quality_score")
    public Double signalQualityScore;
    
    @Column(name = "noise_level_db")
    public Double noiseLevelDb;
    
    @Column(name = "target_strength_db")
    public Double targetStrengthDb;
    
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
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
} 