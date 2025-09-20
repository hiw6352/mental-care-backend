package com.mc.backend.config;

import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "external.apis")
public class ExternalApisProperties {

    private Map<String, Api> clients;

    public Map<String, Api> getClients() {
        return clients;
    }

    public void setClients(Map<String, Api> clients) {
        this.clients = clients;
    }

    public static class Api {

        private String baseUrl;
        private String apiKey; // 있을 수도/없을 수도
        private Integer timeoutMs;
        private Map<String, String> headers;

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }

        public Integer getTimeoutMs() {
            return timeoutMs;
        }

        public void setTimeoutMs(Integer timeoutMs) {
            this.timeoutMs = timeoutMs;
        }

        public Map<String, String> getHeaders() {
            return headers;
        }

        public void setHeaders(Map<String, String> headers) {
            this.headers = headers;
        }
    }
}
