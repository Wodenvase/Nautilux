package com.nautilux.service;

import com.nautilux.model.CoralReef;
import com.nautilux.model.ReefZone;
import com.nautilux.model.SensorData;
import com.nautilux.model.ImageData;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class CoralReefService {
    
    private static final Logger LOG = Logger.getLogger(CoralReefService.class);
    
    @Inject
    RayService rayService;
    
    @Inject
    AlertService alertService;
    
    @ConfigProperty(name = "ray.service.url", defaultValue = "http://localhost:8000")
    String rayServiceUrl;

    public List<CoralReef> findReefs(CoralReef.HealthStatus healthStatus, 
                                   CoralReef.ReefType reefType, 
                                   int page, int size) {
        
        StringBuilder query = new StringBuilder();
        Map<String, Object> params = new HashMap<>();
        
        if (healthStatus != null) {
            query.append("healthStatus = :healthStatus");
            params.put("healthStatus", healthStatus);
        }
        
        if (reefType != null) {
            if (query.length() > 0) query.append(" AND ");
            query.append("reefType = :reefType");
            params.put("reefType", reefType);
        }
        
        if (query.length() == 0) {
            return CoralReef.findAll(Sort.by("name")).page(page, size).list();
        } else {
            return CoralReef.find(query.toString(), params).page(page, size).list();
        }
    }

    public CoralReef findById(Long id) {
        return CoralReef.findById(id);
    }

    @Transactional
    public void persist(CoralReef reef) {
        reef.persist();
        LOG.infof("Created new coral reef: %s", reef.name);
    }

    @Transactional
    public void update(Long id, CoralReef updatedReef) {
        CoralReef reef = findById(id);
        if (reef != null) {
            reef.name = updatedReef.name;
            reef.description = updatedReef.description;
            reef.latitude = updatedReef.latitude;
            reef.longitude = updatedReef.longitude;
            reef.depthMeters = updatedReef.depthMeters;
            reef.reefType = updatedReef.reefType;
            reef.healthStatus = updatedReef.healthStatus;
            reef.healthScore = updatedReef.healthScore;
            reef.bleachingRiskLevel = updatedReef.bleachingRiskLevel;
            reef.biodiversityIndex = updatedReef.biodiversityIndex;
            reef.waterTemperatureCelsius = updatedReef.waterTemperatureCelsius;
            reef.salinityPpt = updatedReef.salinityPpt;
            reef.phLevel = updatedReef.phLevel;
            reef.turbidityNtu = updatedReef.turbidityNtu;
            reef.updatedAt = LocalDateTime.now();
            
            LOG.infof("Updated coral reef: %s", reef.name);
        }
    }

    @Transactional
    public boolean deleteById(Long id) {
        CoralReef reef = findById(id);
        if (reef != null) {
            reef.delete();
            LOG.infof("Deleted coral reef: %s", reef.name);
            return true;
        }
        return false;
    }

    public Map<String, Object> getHealthStatus(Long reefId) {
        CoralReef reef = findById(reefId);
        if (reef == null) {
            return null;
        }
        
        Map<String, Object> healthData = new HashMap<>();
        healthData.put("reefId", reef.id);
        healthData.put("reefName", reef.name);
        healthData.put("healthStatus", reef.healthStatus);
        healthData.put("healthScore", reef.healthScore);
        healthData.put("bleachingRiskLevel", reef.bleachingRiskLevel);
        healthData.put("biodiversityIndex", reef.biodiversityIndex);
        healthData.put("lastUpdated", reef.updatedAt);
        
        // Get latest sensor data
        List<SensorData> latestSensors = SensorData.find(
            "coralReef.id = ?1 ORDER BY timestamp DESC", reefId)
            .page(0, 5).list();
        
        healthData.put("latestSensorData", latestSensors);
        
        // Get latest image analysis
        List<ImageData> latestImages = ImageData.find(
            "coralReef.id = ?1 ORDER BY timestamp DESC", reefId)
            .page(0, 3).list();
        
        healthData.put("latestImageAnalysis", latestImages);
        
        return healthData;
    }

    public Map<String, Object> getMetrics(Long reefId, String startDate, String endDate) {
        CoralReef reef = findById(reefId);
        if (reef == null) {
            return null;
        }
        
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("reefId", reef.id);
        metrics.put("reefName", reef.name);
        
        // Calculate time-based metrics
        LocalDateTime start = startDate != null ? LocalDateTime.parse(startDate) : 
                            LocalDateTime.now().minusDays(30);
        LocalDateTime end = endDate != null ? LocalDateTime.parse(endDate) : 
                          LocalDateTime.now();
        
        // Sensor data metrics
        List<SensorData> sensorData = SensorData.find(
            "coralReef.id = ?1 AND timestamp BETWEEN ?2 AND ?3", 
            reefId, start, end).list();
        
        if (!sensorData.isEmpty()) {
            double avgTemp = sensorData.stream()
                .filter(s -> s.temperatureCelsius != null)
                .mapToDouble(s -> s.temperatureCelsius)
                .average().orElse(0.0);
            
            double avgSalinity = sensorData.stream()
                .filter(s -> s.salinityPpt != null)
                .mapToDouble(s -> s.salinityPpt)
                .average().orElse(0.0);
            
            metrics.put("averageTemperature", avgTemp);
            metrics.put("averageSalinity", avgSalinity);
            metrics.put("sensorReadingsCount", sensorData.size());
        }
        
        // Image analysis metrics
        List<ImageData> imageData = ImageData.find(
            "coralReef.id = ?1 AND timestamp BETWEEN ?2 AND ?3", 
            reefId, start, end).list();
        
        if (!imageData.isEmpty()) {
            double avgCoralCoverage = imageData.stream()
                .filter(i -> i.coralCoveragePercentage != null)
                .mapToDouble(i -> i.coralCoveragePercentage)
                .average().orElse(0.0);
            
            long bleachingImages = imageData.stream()
                .filter(i -> Boolean.TRUE.equals(i.bleachingDetected))
                .count();
            
            metrics.put("averageCoralCoverage", avgCoralCoverage);
            metrics.put("bleachingImagesCount", bleachingImages);
            metrics.put("totalImagesAnalyzed", imageData.size());
        }
        
        return metrics;
    }

    public List<ReefZone> getZones(Long reefId) {
        return ReefZone.find("coralReef.id", reefId).list();
    }

    public List<CoralReef> findNearby(Double latitude, Double longitude, Double radiusKm) {
        // Simple distance calculation (Haversine formula would be better for production)
        // For now, using a rough approximation
        double latRange = radiusKm / 111.0; // 1 degree ≈ 111 km
        double lonRange = radiusKm / (111.0 * Math.cos(Math.toRadians(latitude)));
        
        return CoralReef.find(
            "latitude BETWEEN ?1 AND ?2 AND longitude BETWEEN ?3 AND ?4",
            latitude - latRange, latitude + latRange,
            longitude - lonRange, longitude + lonRange
        ).list();
    }

    public List<Map<String, Object>> getHealthAlerts(String severity, int limit) {
        List<CoralReef> criticalReefs = CoralReef.find(
            "healthStatus IN ('POOR', 'CRITICAL') OR bleachingRiskLevel >= 3"
        ).page(0, limit).list();
        
        return criticalReefs.stream()
            .map(reef -> {
                Map<String, Object> alert = new HashMap<>();
                alert.put("reefId", reef.id);
                alert.put("reefName", reef.name);
                alert.put("healthStatus", reef.healthStatus);
                alert.put("bleachingRiskLevel", reef.bleachingRiskLevel);
                alert.put("lastUpdated", reef.updatedAt);
                alert.put("severity", determineSeverity(reef));
                return alert;
            })
            .toList();
    }

    private String determineSeverity(CoralReef reef) {
        if (reef.healthStatus == CoralReef.HealthStatus.CRITICAL || 
            (reef.bleachingRiskLevel != null && reef.bleachingRiskLevel >= 4)) {
            return "CRITICAL";
        } else if (reef.healthStatus == CoralReef.HealthStatus.POOR || 
                   (reef.bleachingRiskLevel != null && reef.bleachingRiskLevel >= 3)) {
            return "HIGH";
        } else if (reef.healthStatus == CoralReef.HealthStatus.FAIR || 
                   (reef.bleachingRiskLevel != null && reef.bleachingRiskLevel >= 2)) {
            return "MEDIUM";
        }
        return "LOW";
    }

    @Transactional
    public boolean refreshHealthAssessment(Long reefId) {
        CoralReef reef = findById(reefId);
        if (reef == null) {
            return false;
        }
        
        // Trigger health assessment via Ray service
        try {
            Map<String, Object> assessment = rayService.assessReefHealth(reefId);
            if (assessment != null) {
                updateReefHealthFromAssessment(reef, assessment);
                return true;
            }
        } catch (Exception e) {
            LOG.errorf(e, "Failed to refresh health assessment for reef %d", reefId);
        }
        
        return false;
    }

    private void updateReefHealthFromAssessment(CoralReef reef, Map<String, Object> assessment) {
        if (assessment.containsKey("healthScore")) {
            reef.healthScore = (Double) assessment.get("healthScore");
        }
        
        if (assessment.containsKey("healthStatus")) {
            String status = (String) assessment.get("healthStatus");
            reef.healthStatus = CoralReef.HealthStatus.valueOf(status);
        }
        
        if (assessment.containsKey("bleachingRiskLevel")) {
            reef.bleachingRiskLevel = (Integer) assessment.get("bleachingRiskLevel");
        }
        
        if (assessment.containsKey("biodiversityIndex")) {
            reef.biodiversityIndex = (Double) assessment.get("biodiversityIndex");
        }
        
        reef.updatedAt = LocalDateTime.now();
        
        // Check if alert should be triggered
        if (reef.healthStatus == CoralReef.HealthStatus.CRITICAL || 
            (reef.bleachingRiskLevel != null && reef.bleachingRiskLevel >= 4)) {
            alertService.sendHealthAlert(reef);
        }
    }
} 