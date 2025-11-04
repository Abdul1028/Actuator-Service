# Actuator Service - Status Server

A Spring Boot application that acts as a centralized status server, monitoring and aggregating health and metrics from two remote services:

- **api-server**: Hosted at `https://api.wareality.tech`
- **log-service**: Hosted at `https://kafka-log-service-4ebd58d6138e.herokuapp.com`

## Features

- ✅ Aggregated health status from both services
- ✅ Individual service status checks
- ✅ Service info and metrics endpoints
- ✅ Custom Spring Boot Actuator health indicator
- ✅ RESTful API for status monitoring
- ✅ Prometheus metrics support

## Configuration

The service URLs are configured in `application.properties`:

```properties
services.api-server.url=https://api.wareality.tech
services.log-service.url=https://kafka-log-service-4ebd58d6138e.herokuapp.com
```

## API Endpoints

### Overall Status

#### Get Overall Status
```bash
GET /api/status
GET /api/status/health
```

Returns aggregated status of both services with overall health status.

**Response:**
```json
{
  "overallStatus": "UP",
  "timestamp": "2024-01-15T10:30:00",
  "services": [
    {
      "serviceName": "api-server",
      "status": "UP",
      "timestamp": "2024-01-15T10:30:00",
      "details": { ... }
    },
    {
      "serviceName": "log-service",
      "status": "UP",
      "timestamp": "2024-01-15T10:30:00",
      "details": { ... }
    }
  ],
  "totalServices": 2,
  "healthyServices": 2,
  "unhealthyServices": 0
}
```

### API Server Endpoints

#### Get API Server Health
```bash
GET /api/status/api-server
```

#### Get API Server Info
```bash
GET /api/status/api-server/info
```

#### Get API Server Metrics
```bash
GET /api/status/api-server/metrics
```

### Log Service Endpoints

#### Get Log Service Health
```bash
GET /api/status/log-service
```

#### Get Log Service Info
```bash
GET /api/status/log-service/info
```

#### Get Log Service Metrics
```bash
GET /api/status/log-service/metrics
```

#### Get Log Service Detailed Metrics
```bash
GET /api/status/log-service/metrics/details
```

Returns detailed metrics including:
- `logs.consumed`
- `logs.saved`
- `logs.errors`
- `logs.processing.time`
- `logs.total.count`

### Database Health

#### Get Database Health for All Services
```bash
GET /api/status/health/db
```

## Spring Boot Actuator Endpoints

### Health Endpoint
```bash
GET /actuator/health
```

Includes custom health indicator that checks both remote services.

### Info Endpoint
```bash
GET /actuator/info
```

### Metrics Endpoint
```bash
GET /actuator/metrics
```

### Prometheus Endpoint
```bash
GET /actuator/prometheus
```

## Running the Application

### Prerequisites
- Java 17 or higher
- Maven 3.6+

### Build and Run
```bash
# Build the application
mvn clean package

# Run the application
mvn spring-boot:run
```

Or use the Maven wrapper:
```bash
./mvnw spring-boot:run
```

The application will start on port 8080 by default.

## Testing

### Quick Test Script
```bash
#!/bin/bash

BASE_URL="http://localhost:8080"

echo "=== Testing Status Server ==="
echo ""

echo "1. Overall Status:"
curl -s "$BASE_URL/api/status" | jq '.overallStatus' || echo "Failed"
echo ""

echo "2. API Server Health:"
curl -s "$BASE_URL/api/status/api-server" | jq '.status' || echo "Failed"
echo ""

echo "3. Log Service Health:"
curl -s "$BASE_URL/api/status/log-service" | jq '.status' || echo "Failed"
echo ""

echo "4. Log Service Detailed Metrics:"
curl -s "$BASE_URL/api/status/log-service/metrics/details" | jq '.details' || echo "Failed"
echo ""

echo "5. Actuator Health:"
curl -s "$BASE_URL/actuator/health" | jq '.status' || echo "Failed"
echo ""

echo "=== Testing Complete ==="
```

## Architecture

The application consists of:

- **ServiceProperties**: Configuration properties for remote service URLs
- **RemoteServiceChecker**: Service that makes HTTP calls to remote actuator endpoints
- **StatusController**: REST controller exposing status endpoints
- **RemoteServicesHealthIndicator**: Custom Spring Boot Actuator health indicator
- **ServiceStatus**: Model representing individual service status
- **AggregatedStatus**: Model representing aggregated status of all services

## Error Handling

The service handles:
- Network timeouts (configurable via `services.timeout.connect` and `services.timeout.read`)
- Service unavailability
- HTTP errors
- JSON parsing errors

All errors are captured and returned in the status response with appropriate error messages.

## Configuration Properties

| Property | Description | Default |
|----------|-------------|---------|
| `services.api-server.url` | API server base URL | - |
| `services.log-service.url` | Log service base URL | - |
| `services.timeout.connect` | Connection timeout in ms | 5000 |
| `services.timeout.read` | Read timeout in ms | 10000 |

## Monitoring

The service exposes its own actuator endpoints for monitoring:
- Health checks
- Metrics
- Prometheus format for scraping

This allows you to monitor the status server itself while it monitors your other services.

## License

This project is part of the Pushly workspace.

