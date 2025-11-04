package com.example.demo.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "services")
public class ServiceProperties {
    
    private static final Logger logger = LoggerFactory.getLogger(ServiceProperties.class);
    
    private ApiServer apiServer = new ApiServer();
    private LogService logService = new LogService();
    private Timeout timeout = new Timeout();

    public ApiServer getApiServer() {
        return apiServer;
    }

    public void setApiServer(ApiServer apiServer) {
        this.apiServer = apiServer;
    }

    public LogService getLogService() {
        return logService;
    }

    public void setLogService(LogService logService) {
        this.logService = logService;
    }

    public Timeout getTimeout() {
        return timeout;
    }

    public void setTimeout(Timeout timeout) {
        this.timeout = timeout;
    }

    public static class ApiServer {
        private String url;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public static class LogService {
        private String url;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public static class Timeout {
        private int connect = 5000;
        private int read = 10000;

        public int getConnect() {
            return connect;
        }

        public void setConnect(int connect) {
            this.connect = connect;
        }

        public int getRead() {
            return read;
        }

        public void setRead(int read) {
            this.read = read;
        }
    }
    
    @PostConstruct
    public void validate() {
        if (apiServer.getUrl() == null || apiServer.getUrl().isEmpty()) {
            logger.warn("api-server.url is not configured. Please check application.properties");
        } else {
            logger.info("api-server.url configured: {}", apiServer.getUrl());
        }
        
        if (logService.getUrl() == null || logService.getUrl().isEmpty()) {
            logger.warn("log-service.url is not configured. Please check application.properties");
        } else {
            logger.info("log-service.url configured: {}", logService.getUrl());
        }
    }
}

