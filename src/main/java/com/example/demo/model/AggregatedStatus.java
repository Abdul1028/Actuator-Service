package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AggregatedStatus {
    private String overallStatus;
    private LocalDateTime timestamp;
    private List<ServiceStatus> services;
    private int totalServices;
    private int healthyServices;
    private int unhealthyServices;

    public AggregatedStatus() {
        this.timestamp = LocalDateTime.now();
    }

    public String getOverallStatus() {
        return overallStatus;
    }

    public void setOverallStatus(String overallStatus) {
        this.overallStatus = overallStatus;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public List<ServiceStatus> getServices() {
        return services;
    }

    public void setServices(List<ServiceStatus> services) {
        this.services = services;
        if (services != null) {
            this.totalServices = services.size();
            this.healthyServices = (int) services.stream()
                    .filter(s -> "UP".equals(s.getStatus()))
                    .count();
            this.unhealthyServices = totalServices - healthyServices;
            this.overallStatus = unhealthyServices == 0 ? "UP" : "DOWN";
        }
    }

    public int getTotalServices() {
        return totalServices;
    }

    public void setTotalServices(int totalServices) {
        this.totalServices = totalServices;
    }

    public int getHealthyServices() {
        return healthyServices;
    }

    public void setHealthyServices(int healthyServices) {
        this.healthyServices = healthyServices;
    }

    public int getUnhealthyServices() {
        return unhealthyServices;
    }

    public void setUnhealthyServices(int unhealthyServices) {
        this.unhealthyServices = unhealthyServices;
    }
}

