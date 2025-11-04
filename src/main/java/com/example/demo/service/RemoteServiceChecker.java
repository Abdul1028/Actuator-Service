package com.example.demo.service;

import com.example.demo.config.ServiceProperties;
import com.example.demo.model.ServiceStatus;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
public class RemoteServiceChecker {
    
    private static final Logger logger = LoggerFactory.getLogger(RemoteServiceChecker.class);
    
    private final WebClient webClient;
    private final ServiceProperties serviceProperties;
    private final ObjectMapper objectMapper;

    public RemoteServiceChecker(ServiceProperties serviceProperties, ObjectMapper objectMapper) {
        this.serviceProperties = serviceProperties;
        this.objectMapper = objectMapper;
        this.webClient = WebClient.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024))
                .build();
    }

    public ServiceStatus checkApiServerHealth() {
        return checkServiceHealth(
                "api-server",
                serviceProperties.getApiServer().getUrl() + "/actuator/health"
        );
    }

    public ServiceStatus checkApiServerInfo() {
        return checkServiceInfo(
                "api-server",
                serviceProperties.getApiServer().getUrl() + "/actuator/info"
        );
    }

    public ServiceStatus checkApiServerMetrics() {
        return checkServiceMetrics(
                "api-server",
                serviceProperties.getApiServer().getUrl() + "/actuator/metrics"
        );
    }

    public ServiceStatus checkLogServiceHealth() {
        return checkServiceHealth(
                "log-service",
                serviceProperties.getLogService().getUrl() + "/actuator/health"
        );
    }

    public ServiceStatus checkLogServiceInfo() {
        return checkServiceInfo(
                "log-service",
                serviceProperties.getLogService().getUrl() + "/actuator/info"
        );
    }

    public ServiceStatus checkLogServiceMetrics() {
        return checkServiceMetrics(
                "log-service",
                serviceProperties.getLogService().getUrl() + "/actuator/metrics"
        );
    }

    public ServiceStatus checkLogServiceSpecificMetrics() {
        String baseUrl = serviceProperties.getLogService().getUrl();
        ServiceStatus status = new ServiceStatus("log-service", "UP");
        Map<String, Object> metrics = new HashMap<>();
        
        // Check specific log-service metrics
        String[] metricNames = {
            "logs.consumed",
            "logs.saved",
            "logs.errors",
            "logs.processing.time",
            "logs.total.count"
        };
        
        for (String metricName : metricNames) {
            try {
                String response = webClient.get()
                        .uri(baseUrl + "/actuator/metrics/" + metricName)
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .bodyToMono(String.class)
                        .timeout(Duration.ofMillis(serviceProperties.getTimeout().getRead()))
                        .retryWhen(Retry.fixedDelay(1, Duration.ofMillis(1000)))
                        .block();
                
                if (response != null) {
                    JsonNode jsonNode = objectMapper.readTree(response);
                    metrics.put(metricName, jsonNode);
                }
            } catch (Exception e) {
                logger.warn("Failed to fetch metric {} for log-service: {}", metricName, e.getMessage());
                metrics.put(metricName, "unavailable");
            }
        }
        
        status.setDetails(metrics);
        return status;
    }

    private ServiceStatus checkServiceHealth(String serviceName, String url) {
        ServiceStatus status = new ServiceStatus(serviceName, "DOWN");
        
        try {
            String response = webClient.get()
                    .uri(url)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, clientResponse -> {
                        logger.error("Health check failed for {}: {}", serviceName, clientResponse.statusCode());
                        return Mono.error(new RuntimeException("Health check failed: " + clientResponse.statusCode()));
                    })
                    .bodyToMono(String.class)
                    .timeout(Duration.ofMillis(serviceProperties.getTimeout().getRead()))
                    .retryWhen(Retry.fixedDelay(1, Duration.ofMillis(1000)))
                    .block();
            
            if (response != null) {
                JsonNode jsonNode = objectMapper.readTree(response);
                String healthStatus = jsonNode.has("status") ? 
                    jsonNode.get("status").asText() : "UNKNOWN";
                
                status.setStatus(healthStatus.toUpperCase());
                
                // Include details if available
                if (jsonNode.has("components")) {
                    Map<String, Object> details = objectMapper.convertValue(
                        jsonNode.get("components"), 
                        Map.class
                    );
                    status.setDetails(details);
                } else if (jsonNode.has("details")) {
                    Map<String, Object> details = objectMapper.convertValue(
                        jsonNode.get("details"), 
                        Map.class
                    );
                    status.setDetails(details);
                }
            }
        } catch (Exception e) {
            logger.error("Failed to check health for {}: {}", serviceName, e.getMessage());
            status.setError(e.getMessage());
            status.setStatus("DOWN");
        }
        
        return status;
    }

    private ServiceStatus checkServiceInfo(String serviceName, String url) {
        ServiceStatus status = new ServiceStatus(serviceName, "UP");
        
        try {
            String response = webClient.get()
                    .uri(url)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofMillis(serviceProperties.getTimeout().getRead()))
                    .retryWhen(Retry.fixedDelay(1, Duration.ofMillis(1000)))
                    .block();
            
            if (response != null) {
                JsonNode jsonNode = objectMapper.readTree(response);
                Map<String, Object> details = objectMapper.convertValue(jsonNode, Map.class);
                status.setDetails(details);
            }
        } catch (Exception e) {
            logger.error("Failed to fetch info for {}: {}", serviceName, e.getMessage());
            status.setError(e.getMessage());
            status.setStatus("DOWN");
        }
        
        return status;
    }

    private ServiceStatus checkServiceMetrics(String serviceName, String url) {
        ServiceStatus status = new ServiceStatus(serviceName, "UP");
        
        try {
            String response = webClient.get()
                    .uri(url)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofMillis(serviceProperties.getTimeout().getRead()))
                    .retryWhen(Retry.fixedDelay(1, Duration.ofMillis(1000)))
                    .block();
            
            if (response != null) {
                JsonNode jsonNode = objectMapper.readTree(response);
                Map<String, Object> details = new HashMap<>();
                
                if (jsonNode.has("names")) {
                    details.put("availableMetrics", jsonNode.get("names"));
                }
                
                status.setDetails(details);
            }
        } catch (Exception e) {
            logger.error("Failed to fetch metrics for {}: {}", serviceName, e.getMessage());
            status.setError(e.getMessage());
            status.setStatus("DOWN");
        }
        
        return status;
    }
}

