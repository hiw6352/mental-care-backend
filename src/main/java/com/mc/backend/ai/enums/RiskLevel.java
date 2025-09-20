package com.mc.backend.ai.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum RiskLevel {
    OK, WATCH, CONCERN, CRISIS;

    @JsonValue
    public String toJson() {
        return name().toLowerCase();
    }

    @JsonCreator
    public static RiskLevel fromJson(String v) {
        return v == null ? WATCH : RiskLevel.valueOf(v.trim().toUpperCase());
    }
}
