package com.nautilux.api;

import com.nautilux.model.CoralReef;
import com.nautilux.service.CoralReefService;
import io.quarkus.panache.common.Sort;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

@Path("/api/v1/reefs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Coral Reefs", description = "Coral reef management and monitoring")
public class CoralReefResource {

    @Inject
    CoralReefService coralReefService;

    @GET
    @Operation(summary = "Get all coral reefs", description = "Retrieve a list of all coral reefs with optional filtering")
    @APIResponse(responseCode = "200", description = "List of coral reefs", 
                content = @Content(schema = @Schema(implementation = CoralReef.class)))
    public Response getAllReefs(
            @QueryParam("healthStatus") CoralReef.HealthStatus healthStatus,
            @QueryParam("reefType") CoralReef.ReefType reefType,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {
        
        List<CoralReef> reefs = coralReefService.findReefs(healthStatus, reefType, page, size);
        return Response.ok(reefs).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get coral reef by ID", description = "Retrieve a specific coral reef by its ID")
    @APIResponse(responseCode = "200", description = "Coral reef found", 
                content = @Content(schema = @Schema(implementation = CoralReef.class)))
    @APIResponse(responseCode = "404", description = "Coral reef not found")
    public Response getReefById(@PathParam("id") Long id) {
        CoralReef reef = coralReefService.findById(id);
        if (reef == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(reef).build();
    }

    @POST
    @Transactional
    @Operation(summary = "Create new coral reef", description = "Create a new coral reef entry")
    @APIResponse(responseCode = "201", description = "Coral reef created", 
                content = @Content(schema = @Schema(implementation = CoralReef.class)))
    @APIResponse(responseCode = "400", description = "Invalid input data")
    public Response createReef(@Valid CoralReef reef) {
        coralReefService.persist(reef);
        return Response.status(Response.Status.CREATED).entity(reef).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    @Operation(summary = "Update coral reef", description = "Update an existing coral reef")
    @APIResponse(responseCode = "200", description = "Coral reef updated", 
                content = @Content(schema = @Schema(implementation = CoralReef.class)))
    @APIResponse(responseCode = "404", description = "Coral reef not found")
    public Response updateReef(@PathParam("id") Long id, @Valid CoralReef reef) {
        CoralReef existingReef = coralReefService.findById(id);
        if (existingReef == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        
        coralReefService.update(id, reef);
        return Response.ok(existingReef).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    @Operation(summary = "Delete coral reef", description = "Delete a coral reef and all associated data")
    @APIResponse(responseCode = "204", description = "Coral reef deleted")
    @APIResponse(responseCode = "404", description = "Coral reef not found")
    public Response deleteReef(@PathParam("id") Long id) {
        boolean deleted = coralReefService.deleteById(id);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }

    @GET
    @Path("/{id}/health")
    @Operation(summary = "Get reef health status", description = "Get current health status and metrics for a coral reef")
    @APIResponse(responseCode = "200", description = "Health status retrieved")
    @APIResponse(responseCode = "404", description = "Coral reef not found")
    public Response getReefHealth(@PathParam("id") Long id) {
        var healthData = coralReefService.getHealthStatus(id);
        if (healthData == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(healthData).build();
    }

    @GET
    @Path("/{id}/metrics")
    @Operation(summary = "Get reef metrics", description = "Get detailed metrics and analytics for a coral reef")
    @APIResponse(responseCode = "200", description = "Metrics retrieved")
    @APIResponse(responseCode = "404", description = "Coral reef not found")
    public Response getReefMetrics(
            @PathParam("id") Long id,
            @QueryParam("startDate") String startDate,
            @QueryParam("endDate") String endDate) {
        
        var metrics = coralReefService.getMetrics(id, startDate, endDate);
        if (metrics == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(metrics).build();
    }

    @GET
    @Path("/{id}/zones")
    @Operation(summary = "Get reef zones", description = "Get all zones within a coral reef")
    @APIResponse(responseCode = "200", description = "Reef zones retrieved")
    @APIResponse(responseCode = "404", description = "Coral reef not found")
    public Response getReefZones(@PathParam("id") Long id) {
        var zones = coralReefService.getZones(id);
        if (zones == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(zones).build();
    }

    @GET
    @Path("/nearby")
    @Operation(summary = "Find nearby reefs", description = "Find coral reefs within a specified radius")
    @APIResponse(responseCode = "200", description = "Nearby reefs found")
    public Response getNearbyReefs(
            @QueryParam("lat") Double latitude,
            @QueryParam("lon") Double longitude,
            @QueryParam("radiusKm") @DefaultValue("10.0") Double radiusKm) {
        
        if (latitude == null || longitude == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Latitude and longitude are required").build();
        }
        
        var nearbyReefs = coralReefService.findNearby(latitude, longitude, radiusKm);
        return Response.ok(nearbyReefs).build();
    }

    @GET
    @Path("/alerts")
    @Operation(summary = "Get health alerts", description = "Get reefs with health alerts or critical conditions")
    @APIResponse(responseCode = "200", description = "Alerts retrieved")
    public Response getHealthAlerts(
            @QueryParam("severity") String severity,
            @QueryParam("limit") @DefaultValue("10") int limit) {
        
        var alerts = coralReefService.getHealthAlerts(severity, limit);
        return Response.ok(alerts).build();
    }

    @POST
    @Path("/{id}/refresh-health")
    @Operation(summary = "Refresh health assessment", description = "Trigger a fresh health assessment for a coral reef")
    @APIResponse(responseCode = "202", description = "Health assessment initiated")
    @APIResponse(responseCode = "404", description = "Coral reef not found")
    public Response refreshHealthAssessment(@PathParam("id") Long id) {
        boolean initiated = coralReefService.refreshHealthAssessment(id);
        if (!initiated) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.accepted().build();
    }
} 