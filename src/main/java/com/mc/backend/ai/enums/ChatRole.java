package com.mc.backend.ai.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ChatRole {
    SYSTEM, USER, ASSISTANT;

    @JsonValue
    public String toJson() {
        return name().toLowerCase();
    }

    @JsonCreator
    public static ChatRole fromJson(String v) {
        if (v == null) {
            return USER;
        }
        return ChatRole.valueOf(v.trim().toUpperCase());
    }
}