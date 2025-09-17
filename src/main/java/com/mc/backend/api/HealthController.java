package com.mc.backend.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class HealthController {
    @GetMapping("/ping")
    public Map<String, Object> ping() {
        return Map.of("pong", true, "service", "backend", "version", "0.0.1");
    }
}
