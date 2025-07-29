package com.nautilux.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "image_annotations")
public class ImageAnnotation extends PanacheEntity {
    
    @NotNull
    @Column(name = "annotation_type", nullable = false)
    @Enumerated(EnumType.STRING)
    public AnnotationType annotationType;
    
    @Column(name = "species_name")
    public String speciesName;
    
    @Column(name = "common_name")
    public String commonName;
    
    @Column(name = "taxonomic_family")
    public String taxonomicFamily;
    
    @Column(name = "taxonomic_genus")
    public String taxonomicGenus;
    
    @Column(name = "taxonomic_species")
    public String taxonomicSpecies;
    
    @Column(name = "confidence_score")
    public Double confidenceScore;
    
    @Column(name = "bounding_box_x1")
    public Double boundingBoxX1;
    
    @Column(name = "bounding_box_y1")
    public Double boundingBoxY1;
    
    @Column(name = "bounding_box_x2")
    public Double boundingBoxX2;
    
    @Column(name = "bounding_box_y2")
    public Double boundingBoxY2;
    
    @Column(name = "center_x")
    public Double centerX;
    
    @Column(name = "center_y")
    public Double centerY;
    
    @Column(name = "width_pixels")
    public Double widthPixels;
    
    @Column(name = "height_pixels")
    public Double heightPixels;
    
    @Column(name = "area_pixels")
    public Double areaPixels;
    
    @Column(name = "health_status")
    @Enumerated(EnumType.STRING)
    public CoralReef.HealthStatus healthStatus;
    
    @Column(name = "bleaching_severity")
    public Integer bleachingSeverity;
    
    @Column(name = "size_category")
    public String sizeCategory;
    
    @Column(name = "color_description")
    public String colorDescription;
    
    @Column(name = "morphology_description")
    public String morphologyDescription;
    
    @Column(name = "ai_model_version")
    public String aiModelVersion;
    
    @Column(name = "created_at", nullable = false)
    public LocalDateTime createdAt = LocalDateTime.now();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_data_id", nullable = false)
    public ImageData imageData;
    
    public enum AnnotationType {
        CORAL, FISH, INVERTEBRATE, ALGAE, SAND, ROCK, DEBRIS, BLEACHED_CORAL, 
        DISEASED_CORAL, SPONGE, ANEMONE, STARFISH, URCHIN, CRAB, LOBSTER, 
        SHRIMP, CLAM, OYSTER, MUSSEL, OTHER
    }
} 