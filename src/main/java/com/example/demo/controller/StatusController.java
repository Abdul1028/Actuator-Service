package com.example.demo.controller;

import com.example.demo.model.AggregatedStatus;
import com.example.demo.model.ServiceStatus;
import com.example.demo.service.RemoteServiceChecker;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/status")
public class StatusController {
    
    private final RemoteServiceChecker remoteServiceChecker;

    public StatusController(RemoteServiceChecker remoteServiceChecker) {
        this.remoteServiceChecker = remoteServiceChecker;
    }

    @GetMapping
    public ResponseEntity<AggregatedStatus> getOverallStatus() {
        AggregatedStatus aggregatedStatus = new AggregatedStatus();
        
        ServiceStatus apiServerStatus = remoteServiceChecker.checkApiServerHealth();
        ServiceStatus logServiceStatus = remoteServiceChecker.checkLogServiceHealth();
        
        List<ServiceStatus> services = Arrays.asList(apiServerStatus, logServiceStatus);
        aggregatedStatus.setServices(services);
        
        return ResponseEntity.ok(aggregatedStatus);
    }

    @GetMapping("/health")
    public ResponseEntity<AggregatedStatus> getHealthStatus() {
        return getOverallStatus();
    }

    @GetMapping("/api-server")
    public ResponseEntity<ServiceStatus> getApiServerStatus() {
        ServiceStatus status = remoteServiceChecker.checkApiServerHealth();
        return ResponseEntity.ok(status);
    }

    @GetMapping("/api-server/info")
    public ResponseEntity<ServiceStatus> getApiServerInfo() {
        ServiceStatus status = remoteServiceChecker.checkApiServerInfo();
        return ResponseEntity.ok(status);
    }

    @GetMapping("/api-server/metrics")
    public ResponseEntity<ServiceStatus> getApiServerMetrics() {
        ServiceStatus status = remoteServiceChecker.checkApiServerMetrics();
        return ResponseEntity.ok(status);
    }

    @GetMapping("/log-service")
    public ResponseEntity<ServiceStatus> getLogServiceStatus() {
        ServiceStatus status = remoteServiceChecker.checkLogServiceHealth();
        return ResponseEntity.ok(status);
    }

    @GetMapping("/log-service/info")
    public ResponseEntity<ServiceStatus> getLogServiceInfo() {
        ServiceStatus status = remoteServiceChecker.checkLogServiceInfo();
        return ResponseEntity.ok(status);
    }

    @GetMapping("/log-service/metrics")
    public ResponseEntity<ServiceStatus> getLogServiceMetrics() {
        ServiceStatus status = remoteServiceChecker.checkLogServiceMetrics();
        return ResponseEntity.ok(status);
    }

    @GetMapping("/log-service/metrics/details")
    public ResponseEntity<ServiceStatus> getLogServiceDetailedMetrics() {
        ServiceStatus status = remoteServiceChecker.checkLogServiceSpecificMetrics();
        return ResponseEntity.ok(status);
    }

    @GetMapping("/health/db")
    public ResponseEntity<AggregatedStatus> getDatabaseHealth() {
        AggregatedStatus aggregatedStatus = new AggregatedStatus();
        
        // Check database health for both services
        ServiceStatus apiServerStatus = remoteServiceChecker.checkApiServerHealth();
        ServiceStatus logServiceStatus = remoteServiceChecker.checkLogServiceHealth();
        
        // Extract database health from details if available
        List<ServiceStatus> services = Arrays.asList(apiServerStatus, logServiceStatus);
        aggregatedStatus.setServices(services);
        
        return ResponseEntity.ok(aggregatedStatus);
    }
}

