package com.mc.backend.ai.dto;

public record JsonSchema(String name, SchemaRoot schema, Boolean strict) {

    /**
     * 스키마 루트 타입(구체 스키마 클래스들이 구현)
     */
    public interface SchemaRoot {

    }
}
