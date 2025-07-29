package com.nautilux.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "sensor_data")
public class SensorData extends PanacheEntity {
    
    @NotNull
    @Column(name = "timestamp", nullable = false)
    public LocalDateTime timestamp;
    
    @NotNull
    @Column(name = "sensor_type", nullable = false)
    @Enumerated(EnumType.STRING)
    public SensorType sensorType;
    
    @Column(name = "sensor_id")
    public String sensorId;
    
    @Column(name = "latitude")
    public Double latitude;
    
    @Column(name = "longitude")
    public Double longitude;
    
    @Column(name = "depth_meters")
    public Double depthMeters;
    
    @Column(name = "temperature_celsius")
    public Double temperatureCelsius;
    
    @Column(name = "salinity_ppt")
    public Double salinityPpt;
    
    @Column(name = "ph_level")
    public Double phLevel;
    
    @Column(name = "dissolved_oxygen_mg_l")
    public Double dissolvedOxygenMgL;
    
    @Column(name = "turbidity_ntu")
    public Double turbidityNtu;
    
    @Column(name = "conductivity_ms_cm")
    public Double conductivityMsCm;
    
    @Column(name = "pressure_bar")
    public Double pressureBar;
    
    @Column(name = "light_intensity_lux")
    public Double lightIntensityLux;
    
    @Column(name = "current_speed_ms")
    public Double currentSpeedMs;
    
    @Column(name = "current_direction_degrees")
    public Double currentDirectionDegrees;
    
    @Column(name = "wave_height_meters")
    public Double waveHeightMeters;
    
    @Column(name = "wave_period_seconds")
    public Double wavePeriodSeconds;
    
    @Column(name = "raw_value")
    public String rawValue;
    
    @Column(name = "unit")
    public String unit;
    
    @Column(name = "quality_score")
    public Double qualityScore;
    
    @Column(name = "created_at", nullable = false)
    public LocalDateTime createdAt = LocalDateTime.now();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coral_reef_id")
    public CoralReef coralReef;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reef_zone_id")
    public ReefZone reefZone;
    
    public enum SensorType {
        TEMPERATURE, SALINITY, PH, DISSOLVED_OXYGEN, TURBIDITY, 
        CONDUCTIVITY, PRESSURE, LIGHT, CURRENT, WAVE, 
        MULTI_PARAMETER, WEATHER, GPS
    }
} 