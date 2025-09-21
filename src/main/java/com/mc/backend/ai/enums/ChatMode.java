package com.mc.backend.ai.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ChatMode {
    CASUAL, SUPPORTIVE, COUNSELING, CRISIS;

    @JsonValue
    public String toJson() {
        return name().toLowerCase();
    }

    @JsonCreator
    public static ChatMode fromJson(String v) {
        return v == null ? CASUAL : ChatMode.valueOf(v.trim().toUpperCase());
    }
}
