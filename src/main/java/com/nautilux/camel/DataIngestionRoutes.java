package com.nautilux.camel;

import com.nautilux.model.SensorData;
import com.nautilux.model.ImageData;
import com.nautilux.model.SonarData;
import com.nautilux.service.DataProcessingService;
import com.nautilux.service.StorageService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.time.LocalDateTime;

@ApplicationScoped
public class DataIngestionRoutes extends RouteBuilder {
    
    private static final Logger LOG = Logger.getLogger(DataIngestionRoutes.class);
    
    @Inject
    DataProcessingService dataProcessingService;
    
    @Inject
    StorageService storageService;
    
    @ConfigProperty(name = "camel.file.watch-directory", defaultValue = "./data/incoming")
    String watchDirectory;
    
    @ConfigProperty(name = "camel.ftp.host", defaultValue = "localhost")
    String ftpHost;
    
    @ConfigProperty(name = "camel.ftp.port", defaultValue = "21")
    int ftpPort;
    
    @ConfigProperty(name = "camel.ftp.username", defaultValue = "anonymous")
    String ftpUsername;
    
    @ConfigProperty(name = "camel.ftp.password", defaultValue = "anonymous")
    String ftpPassword;

    @Override
    public void configure() throws Exception {
        
        // Error handling
        errorHandler(deadLetterChannel("direct:error-handler")
            .maximumRedeliveries(3)
            .redeliveryDelay(1000)
            .backOffMultiplier(2)
            .useExponentialBackOff());
        
        // Error handler route
        from("direct:error-handler")
            .log("Error processing message: ${body}")
            .to("log:error?level=ERROR");
        
        // Timer-based health check route
        from("timer:health-check?period=300000") // Every 5 minutes
            .routeId("health-check-timer")
            .log("Performing scheduled health check")
            .to("direct:trigger-health-assessment");
        
        // Health assessment trigger
        from("direct:trigger-health-assessment")
            .routeId("health-assessment-trigger")
            .log("Triggering health assessment for all reefs")
            .bean(dataProcessingService, "triggerHealthAssessment");
        
        // File-based sensor data ingestion
        from("file:" + watchDirectory + "/sensors?include=.*\\.(csv|json)&move=processed&moveFailed=failed")
            .routeId("sensor-data-ingestion")
            .log("Processing sensor data file: ${header.CamelFileName}")
            .choice()
                .when(header("CamelFileName").endsWith(".csv"))
                    .unmarshal().csv()
                    .split(body())
                    .bean(dataProcessingService, "processSensorDataCsv")
                .when(header("CamelFileName").endsWith(".json"))
                    .unmarshal().json(JsonLibrary.Jackson)
                    .bean(dataProcessingService, "processSensorDataJson")
            .end()
            .log("Sensor data processing completed");
        
        // FTP sensor data ingestion
        from("ftp://" + ftpUsername + "@" + ftpHost + ":" + ftpPort + "/sensors?password=" + ftpPassword + "&include=.*\\.(csv|json)&move=processed&moveFailed=failed&delay=60000")
            .routeId("ftp-sensor-ingestion")
            .log("Processing FTP sensor data: ${header.CamelFileName}")
            .choice()
                .when(header("CamelFileName").endsWith(".csv"))
                    .unmarshal().csv()
                    .split(body())
                    .bean(dataProcessingService, "processSensorDataCsv")
                .when(header("CamelFileName").endsWith(".json"))
                    .unmarshal().json(JsonLibrary.Jackson)
                    .bean(dataProcessingService, "processSensorDataJson")
            .end()
            .log("FTP sensor data processing completed");
        
        // Image data ingestion
        from("file:" + watchDirectory + "/images?include=.*\\.(jpg|jpeg|png|tiff|tif)&move=processed&moveFailed=failed")
            .routeId("image-ingestion")
            .log("Processing image file: ${header.CamelFileName}")
            .bean(storageService, "storeImage")
            .bean(dataProcessingService, "processImageData")
            .log("Image processing completed");
        
        // FTP image ingestion
        from("ftp://" + ftpUsername + "@" + ftpHost + ":" + ftpPort + "/images?password=" + ftpPassword + "&include=.*\\.(jpg|jpeg|png|tiff|tif)&move=processed&moveFailed=failed&delay=60000")
            .routeId("ftp-image-ingestion")
            .log("Processing FTP image: ${header.CamelFileName}")
            .bean(storageService, "storeImage")
            .bean(dataProcessingService, "processImageData")
            .log("FTP image processing completed");
        
        // Sonar data ingestion
        from("file:" + watchDirectory + "/sonar?include=.*\\.(csv|json|bin)&move=processed&moveFailed=failed")
            .routeId("sonar-ingestion")
            .log("Processing sonar data file: ${header.CamelFileName}")
            .choice()
                .when(header("CamelFileName").endsWith(".csv"))
                    .unmarshal().csv()
                    .bean(dataProcessingService, "processSonarDataCsv")
                .when(header("CamelFileName").endsWith(".json"))
                    .unmarshal().json(JsonLibrary.Jackson)
                    .bean(dataProcessingService, "processSonarDataJson")
                .when(header("CamelFileName").endsWith(".bin"))
                    .bean(dataProcessingService, "processSonarDataBinary")
            .end()
            .log("Sonar data processing completed");
        
        // FTP sonar ingestion
        from("ftp://" + ftpUsername + "@" + ftpHost + ":" + ftpPort + "/sonar?password=" + ftpPassword + "&include=.*\\.(csv|json|bin)&move=processed&moveFailed=failed&delay=60000")
            .routeId("ftp-sonar-ingestion")
            .log("Processing FTP sonar data: ${header.CamelFileName}")
            .choice()
                .when(header("CamelFileName").endsWith(".csv"))
                    .unmarshal().csv()
                    .bean(dataProcessingService, "processSonarDataCsv")
                .when(header("CamelFileName").endsWith(".json"))
                    .unmarshal().json(JsonLibrary.Jackson)
                    .bean(dataProcessingService, "processSonarDataJson")
                .when(header("CamelFileName").endsWith(".bin"))
                    .bean(dataProcessingService, "processSonarDataBinary")
            .end()
            .log("FTP sonar data processing completed");
        
        // HTTP REST API for real-time data ingestion
        from("platform-http:/api/v1/ingest/sensor?httpMethodRestrict=POST")
            .routeId("http-sensor-ingestion")
            .log("Processing HTTP sensor data")
            .unmarshal().json(JsonLibrary.Jackson)
            .bean(dataProcessingService, "processSensorDataJson")
            .setBody(simple("{\"status\": \"success\", \"message\": \"Sensor data processed successfully\"}"))
            .setHeader("Content-Type", constant("application/json"));
        
        from("platform-http:/api/v1/ingest/image?httpMethodRestrict=POST")
            .routeId("http-image-ingestion")
            .log("Processing HTTP image data")
            .bean(storageService, "storeImageFromHttp")
            .bean(dataProcessingService, "processImageData")
            .setBody(simple("{\"status\": \"success\", \"message\": \"Image processed successfully\"}"))
            .setHeader("Content-Type", constant("application/json"));
        
        from("platform-http:/api/v1/ingest/sonar?httpMethodRestrict=POST")
            .routeId("http-sonar-ingestion")
            .log("Processing HTTP sonar data")
            .unmarshal().json(JsonLibrary.Jackson)
            .bean(dataProcessingService, "processSonarDataJson")
            .setBody(simple("{\"status\": \"success\", \"message\": \"Sonar data processed successfully\"}"))
            .setHeader("Content-Type", constant("application/json"));
        
        // External API data ingestion (NOAA, CoralNet, MBARI)
        from("timer:external-api-poll?period=3600000") // Every hour
            .routeId("external-api-poll")
            .log("Polling external APIs for data")
            // .parallelProcessing() // Not available in Camel Quarkus 3.x
            .to("direct:noaa-data-poll", "direct:coralnet-data-poll", "direct:mbari-data-poll");
        
        from("direct:noaa-data-poll")
            .routeId("noaa-data-poll")
            .log("Polling NOAA Coral Reef Watch data")
            .bean(dataProcessingService, "fetchNoaaData");
        
        from("direct:coralnet-data-poll")
            .routeId("coralnet-data-poll")
            .log("Polling CoralNet data")
            .bean(dataProcessingService, "fetchCoralNetData");
        
        from("direct:mbari-data-poll")
            .routeId("mbari-data-poll")
            .log("Polling MBARI sonar data")
            .bean(dataProcessingService, "fetchMbariData");
        
        // Data validation and enrichment
        from("direct:validate-sensor-data")
            .routeId("sensor-data-validation")
            .log("Validating sensor data")
            .bean(dataProcessingService, "validateSensorData")
            .choice()
                .when(header("validationStatus").isEqualTo("VALID"))
                    .to("direct:enrich-sensor-data")
                .otherwise()
                    .log("Invalid sensor data: ${body}")
                    .to("direct:error-handler")
            .end();
        
        from("direct:enrich-sensor-data")
            .routeId("sensor-data-enrichment")
            .log("Enriching sensor data")
            .bean(dataProcessingService, "enrichSensorData")
            .to("direct:store-sensor-data");
        
        from("direct:store-sensor-data")
            .routeId("sensor-data-storage")
            .log("Storing sensor data")
            .bean(dataProcessingService, "storeSensorData");
        
        // Alert triggering based on data thresholds
        from("direct:check-alerts")
            .routeId("alert-check")
            .log("Checking for alert conditions")
            .bean(dataProcessingService, "checkAlertConditions")
            .choice()
                .when(header("alertRequired").isEqualTo(true))
                    .log("Alert condition detected: ${body}")
                    .to("direct:trigger-alert")
                .otherwise()
                    .log("No alert conditions detected")
            .end();
        
        from("direct:trigger-alert")
            .routeId("alert-trigger")
            .log("Triggering alert: ${body}")
            .bean(dataProcessingService, "sendAlert");
    }
} 