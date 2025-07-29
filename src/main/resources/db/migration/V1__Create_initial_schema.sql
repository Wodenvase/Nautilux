-- Nautilux Database Schema
-- Initial migration to create all tables

-- Coral Reefs table
CREATE TABLE coral_reefs (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    depth_meters DOUBLE PRECISION,
    reef_type VARCHAR(50),
    health_status VARCHAR(50),
    health_score DOUBLE PRECISION DEFAULT 0.0,
    bleaching_risk_level INTEGER DEFAULT 1,
    biodiversity_index DOUBLE PRECISION DEFAULT 0.0,
    water_temperature_celsius DOUBLE PRECISION,
    salinity_ppt DOUBLE PRECISION,
    ph_level DOUBLE PRECISION,
    turbidity_ntu DOUBLE PRECISION,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Reef Zones table
CREATE TABLE reef_zones (
    id BIGSERIAL PRIMARY KEY,
    reef_id BIGINT REFERENCES coral_reefs(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    zone_type VARCHAR(50),
    depth_range_min DOUBLE PRECISION,
    depth_range_max DOUBLE PRECISION,
    coral_coverage_percentage DOUBLE PRECISION,
    fish_density_estimate DOUBLE PRECISION,
    health_score DOUBLE PRECISION DEFAULT 0.0,
    bleaching_risk_level INTEGER DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Sensor Data table
CREATE TABLE sensor_data (
    id BIGSERIAL PRIMARY KEY,
    reef_id BIGINT REFERENCES coral_reefs(id) ON DELETE CASCADE,
    zone_id BIGINT REFERENCES reef_zones(id) ON DELETE CASCADE,
    sensor_type VARCHAR(50) NOT NULL,
    sensor_id VARCHAR(255),
    temperature_celsius DOUBLE PRECISION,
    salinity_ppt DOUBLE PRECISION,
    ph_level DOUBLE PRECISION,
    turbidity_ntu DOUBLE PRECISION,
    dissolved_oxygen_mg_l DOUBLE PRECISION,
    conductivity_ms_cm DOUBLE PRECISION,
    pressure_bar DOUBLE PRECISION,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    location_latitude DOUBLE PRECISION,
    location_longitude DOUBLE PRECISION,
    data_quality_score DOUBLE PRECISION DEFAULT 1.0
);

-- Image Data table
CREATE TABLE image_data (
    id BIGSERIAL PRIMARY KEY,
    reef_id BIGINT REFERENCES coral_reefs(id) ON DELETE CASCADE,
    zone_id BIGINT REFERENCES reef_zones(id) ON DELETE CASCADE,
    file_path VARCHAR(500) NOT NULL,
    file_name VARCHAR(255),
    file_size_bytes BIGINT,
    image_width INTEGER,
    image_height INTEGER,
    capture_timestamp TIMESTAMP,
    camera_type VARCHAR(100),
    depth_meters DOUBLE PRECISION,
    water_conditions TEXT,
    analysis_status VARCHAR(50) DEFAULT 'PENDING',
    coral_coverage_percentage DOUBLE PRECISION,
    bleaching_detected BOOLEAN DEFAULT FALSE,
    bleaching_severity INTEGER DEFAULT 0,
    health_score DOUBLE PRECISION DEFAULT 0.0,
    biodiversity_score DOUBLE PRECISION DEFAULT 0.0,
    confidence_score DOUBLE PRECISION DEFAULT 0.0,
    analysis_timestamp TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Sonar Data table
CREATE TABLE sonar_data (
    id BIGSERIAL PRIMARY KEY,
    reef_id BIGINT REFERENCES coral_reefs(id) ON DELETE CASCADE,
    zone_id BIGINT REFERENCES reef_zones(id) ON DELETE CASCADE,
    file_path VARCHAR(500) NOT NULL,
    file_name VARCHAR(255),
    file_size_bytes BIGINT,
    sonar_type VARCHAR(100),
    frequency_hz DOUBLE PRECISION,
    depth_meters DOUBLE PRECISION,
    range_meters DOUBLE PRECISION,
    capture_timestamp TIMESTAMP,
    analysis_status VARCHAR(50) DEFAULT 'PENDING',
    reef_structure_detected BOOLEAN DEFAULT FALSE,
    structure_height_meters DOUBLE PRECISION,
    structure_complexity_score DOUBLE PRECISION DEFAULT 0.0,
    fish_density_estimate DOUBLE PRECISION DEFAULT 0.0,
    sediment_thickness_meters DOUBLE PRECISION,
    water_column_height_meters DOUBLE PRECISION,
    signal_quality_score DOUBLE PRECISION DEFAULT 0.0,
    health_score DOUBLE PRECISION DEFAULT 0.0,
    confidence_score DOUBLE PRECISION DEFAULT 0.0,
    analysis_timestamp TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Image Annotations table
CREATE TABLE image_annotations (
    id BIGSERIAL PRIMARY KEY,
    image_id BIGINT REFERENCES image_data(id) ON DELETE CASCADE,
    annotation_type VARCHAR(50) NOT NULL,
    species_name VARCHAR(255),
    confidence_score DOUBLE PRECISION DEFAULT 0.0,
    bounding_box_x1 DOUBLE PRECISION,
    bounding_box_y1 DOUBLE PRECISION,
    bounding_box_x2 DOUBLE PRECISION,
    bounding_box_y2 DOUBLE PRECISION,
    center_x DOUBLE PRECISION,
    center_y DOUBLE PRECISION,
    area_pixels INTEGER,
    color_analysis JSONB,
    health_indicator VARCHAR(50),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better performance
CREATE INDEX idx_coral_reefs_health_status ON coral_reefs(health_status);
CREATE INDEX idx_coral_reefs_reef_type ON coral_reefs(reef_type);
CREATE INDEX idx_reef_zones_reef_id ON reef_zones(reef_id);
CREATE INDEX idx_sensor_data_reef_id ON sensor_data(reef_id);
CREATE INDEX idx_sensor_data_timestamp ON sensor_data(timestamp);
CREATE INDEX idx_image_data_reef_id ON image_data(reef_id);
CREATE INDEX idx_image_data_analysis_status ON image_data(analysis_status);
CREATE INDEX idx_sonar_data_reef_id ON sonar_data(reef_id);
CREATE INDEX idx_sonar_data_analysis_status ON sonar_data(analysis_status);
CREATE INDEX idx_image_annotations_image_id ON image_annotations(image_id);
CREATE INDEX idx_image_annotations_species ON image_annotations(species_name); 