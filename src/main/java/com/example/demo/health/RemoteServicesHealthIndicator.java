package com.example.demo.health;

import com.example.demo.service.RemoteServiceChecker;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class RemoteServicesHealthIndicator implements HealthIndicator {
    
    private final RemoteServiceChecker remoteServiceChecker;

    public RemoteServicesHealthIndicator(RemoteServiceChecker remoteServiceChecker) {
        this.remoteServiceChecker = remoteServiceChecker;
    }

    @Override
    public Health health() {
        try {
            var apiServerStatus = remoteServiceChecker.checkApiServerHealth();
            var logServiceStatus = remoteServiceChecker.checkLogServiceHealth();
            
            boolean apiServerUp = "UP".equals(apiServerStatus.getStatus());
            boolean logServiceUp = "UP".equals(logServiceStatus.getStatus());
            
            int healthyCount = (apiServerUp ? 1 : 0) + (logServiceUp ? 1 : 0);
            
            if (apiServerUp && logServiceUp) {
                return Health.up()
                        .withDetail("api-server", apiServerStatus.getStatus())
                        .withDetail("log-service", logServiceStatus.getStatus())
                        .withDetail("totalServices", 2)
                        .withDetail("healthyServices", 2)
                        .build();
            } else {
                return Health.down()
                        .withDetail("api-server", apiServerStatus.getStatus())
                        .withDetail("log-service", logServiceStatus.getStatus())
                        .withDetail("totalServices", 2)
                        .withDetail("healthyServices", healthyCount)
                        .build();
            }
        } catch (Exception e) {
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}

